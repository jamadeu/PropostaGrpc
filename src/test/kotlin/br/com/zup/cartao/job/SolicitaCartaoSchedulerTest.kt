package br.com.zup.cartao.job

import br.com.zup.cartao.clienteHttp.HttpClientCartao
import br.com.zup.cartao.clienteHttp.SolicitaCartaoRequest
import br.com.zup.cartao.clienteHttp.SolicitaCartaoResponse
import br.com.zup.cartao.vencimento.VencimentoResponse
import br.com.zup.proposta.Proposta
import br.com.zup.proposta.PropostaRepository
import br.com.zup.proposta.StatusProposta
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class SolicitaCartaoSchedulerTest(
    private val repository: PropostaRepository
) {
    @Inject
    lateinit var cartaoClient: HttpClientCartao

    private lateinit var proposta: Proposta

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        proposta = repository.save(
            Proposta(
                documento = "417.533.020-80",
                email = "email@test.com",
                nome = "Cliente",
                endereco = "Endereco",
                salario = BigDecimal(2000.00),
                propostaElegivel = true,
                statusProposta = StatusProposta.PENDENTE
            )
        )
    }

    @Test
    fun `Cria cartao`() {
        val solicitaCartaoResponse = solicitaCartaoResponse()
        `when`(cartaoClient.solicitaCartao(solicitaCartaoRequest()))
            .thenReturn(HttpResponse.ok(solicitaCartaoResponse))

        SolicitaCartaoScheduler(repository, cartaoClient).solicitaCartao()
        val propostaAtualizada = repository.findById(proposta.id!!).orElseThrow()

        with(propostaAtualizada) {
            val cartao = this.cartao
            assertEquals(statusProposta, StatusProposta.CONCLUIDA)
            assertNotNull(cartao)
            assertEquals(cartao!!.numero, solicitaCartaoResponse.numeroCartao)
            assertTrue(repository.findAllPendenteCartao().isEmpty())
        }
    }

    private fun solicitaCartaoResponse() = SolicitaCartaoResponse(
        numeroCartao = UUID.randomUUID().toString(),
        titular = proposta.nome,
        emitidoEm = LocalDateTime.now(),
        limite = 1000.00,
        vencimento = VencimentoResponse(
            idVencimento = UUID.randomUUID().toString(),
            dia = 1,
            dataDeCriacao = LocalDateTime.now()
        ),
        renegociacao = null,
        idProposta = proposta.id.toString()
    )

    private fun solicitaCartaoRequest() = SolicitaCartaoRequest(
        documento = proposta.documento,
        nome = proposta.nome,
        idProposta = proposta.id.toString()
    )

    @MockBean(HttpClientCartao::class)
    fun cartaoClient(): HttpClientCartao {
        return Mockito.mock(HttpClientCartao::class.java)
    }

}
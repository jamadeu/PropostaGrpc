package br.com.zup.proposta.consulta

import br.com.zup.ConsultaPropostaRequest
import br.com.zup.PropostaGrpcServiceGrpc
import br.com.zup.StatusProposta
import br.com.zup.proposta.Proposta
import br.com.zup.proposta.PropostaRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.*

@MicronautTest(transactional = false)
internal class PropostaEndpointTest(
    private val repository: PropostaRepository,
    private val grpcClient: PropostaGrpcServiceGrpc.PropostaGrpcServiceBlockingStub
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `Retorna detalhes da proposta`() {
        val proposta = repository.save(
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

        val response = grpcClient.consulta(consultaPropostaRequest(proposta.id.toString()))

        with(response) {
            assertEquals(idProposta, proposta.id.toString())
            assertEquals(nome, proposta.nome)
            assertEquals(documento, proposta.documento)
            assertEquals(endereco, proposta.endereco)
            assertEquals(salario, proposta.salario.toDouble())
            assertEquals(status, proposta.statusProposta)
            assertTrue(elegivel)
            assertEquals(cartao, "Nao possui cartao")
        }
    }

    @Test
    fun `Retorna INVALID_ARGUMENT quando proposta nao existe`() {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(
                consultaPropostaRequest(UUID.randomUUID().toString())
            )
        }

        with(response) {
            assertNotNull(this)
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertTrue(message!!.contains("Proposta nao localizada"))
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["idInvalido"])
    fun `Retorna INVALID_ARGUMENT quando idProposta nulo, vazio ou nao for UUID`(id: String?) {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(
                consultaPropostaRequest(id)
            )
        }
    }

    private fun consultaPropostaRequest(id: String?): ConsultaPropostaRequest {
        return if (id == null) {
            ConsultaPropostaRequest.newBuilder().build()
        } else {
            ConsultaPropostaRequest.newBuilder()
                .setIdProposta(id)
                .build()
        }
    }
}
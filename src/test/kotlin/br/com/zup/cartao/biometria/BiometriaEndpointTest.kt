package br.com.zup.cartao.biometria

import br.com.zup.BiometriaGrpcServiceGrpc
import br.com.zup.CadastraBiometriaRequest
import br.com.zup.StatusProposta
import br.com.zup.cartao.Cartao
import br.com.zup.cartao.CartaoRepository
import br.com.zup.cartao.vencimento.Vencimento
import br.com.zup.proposta.Proposta
import br.com.zup.proposta.PropostaRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BiometriaEndpointTest(
    @Inject val cartaoRepository: CartaoRepository,
    @Inject val propostaRepository: PropostaRepository,
    @Inject val grpcClient: BiometriaGrpcServiceGrpc.BiometriaGrpcServiceBlockingStub
) {

    private lateinit var cartao: Cartao

    @BeforeEach
    fun setup() {
        val proposta = propostaRepository.save(
            Proposta(
                documento = "809.455.710-97",
                email = "email@teste.com",
                nome = "Titular",
                endereco = "Endereco",
                salario = BigDecimal(2000),
                propostaElegivel = true,
                statusProposta = StatusProposta.PENDENTE,
                cartao = null
            )
        )

        cartao = cartaoRepository.save(
            Cartao(
                numero = "1111-2222-3333-4444",
                titular = "Titular",
                limite = BigDecimal(1000),
                proposta = proposta,
                emitidoEm = LocalDateTime.now(),
                vencimento = Vencimento(
                    idVencimento = UUID.randomUUID().toString(),
                    dia = 2,
                    dataCriacao = LocalDateTime.now()
                ),
                renegociacao = null,
                parcelas = mutableSetOf(),
                carteiras = mutableSetOf(),
                avisos = mutableSetOf(),
                bloqueios = mutableSetOf(),
                biometrias = mutableSetOf()
            )
        )
    }

    @AfterEach
    fun cleanUp() {
        cartaoRepository.deleteAll()
        propostaRepository.deleteAll()
    }

    @Test
    fun `Cadastra biometria`() {
        val response = grpcClient.cadastra(
            cadastraBiometriaRequest(biometria = "Biometria", idCartao = cartao.id.toString())
        )


        val cartaoAtualizado = cartaoRepository.findById(cartao.id!!).orElseThrow()
        val biometria = cartaoAtualizado.biometrias.first()
        assertNotNull(response)
        assertEquals(cartaoAtualizado.biometrias.size, 1)
        assertEquals(response.idBiometria, biometria.id.toString())
    }


    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `Retorna INVALID_ARGUMENT quando biometria nula ou vazia`(biometria: String?) {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(cadastraBiometriaRequest(biometria = biometria, idCartao = cartao.id.toString()))
        }

        with(response) {
            assertNotNull(this)
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertTrue(message!!.contains("Biometria nao pode ser nula ou vazia"))
            val cartaoAtualizado = cartaoRepository.findById(cartao.id!!).orElseThrow()
            assertTrue(cartaoAtualizado.biometrias.isEmpty())
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["idCartaoInvalido"])
    fun `Retorna INVALID_ARGUMENT quando idCartao nulo, vazio ou nao for UUID`(idCartao: String?) {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(cadastraBiometriaRequest(biometria = "Biometria", idCartao = idCartao))
        }

        with(response) {
            assertNotNull(this)
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            val cartaoAtualizado = cartaoRepository.findById(cartao.id!!).orElseThrow()
            assertTrue(cartaoAtualizado.biometrias.isEmpty())
        }
    }

    @Test
    fun `Retorna NOT_FOUND quando cartao nao existe`() {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(
                cadastraBiometriaRequest(biometria = "Biometria", idCartao = UUID.randomUUID().toString())
            )
        }

        with(response) {
            assertNotNull(this)
            assertEquals(status.code, Status.NOT_FOUND.code)
            assertTrue(message!!.contains("Cartao nao localizado"))
        }
    }

    private fun cadastraBiometriaRequest(biometria: String?, idCartao: String?): CadastraBiometriaRequest {
        return when {
            biometria == null -> CadastraBiometriaRequest.newBuilder()
                .setIdCartao(idCartao)
                .build()
            idCartao == null -> CadastraBiometriaRequest.newBuilder()
                .setBiometria(biometria)
                .build()
            else -> CadastraBiometriaRequest.newBuilder()
                .setBiometria(biometria)
                .setIdCartao(idCartao)
                .build()
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                BiometriaGrpcServiceGrpc.BiometriaGrpcServiceBlockingStub {
            return BiometriaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
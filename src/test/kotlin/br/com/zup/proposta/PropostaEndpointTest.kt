package br.com.zup.proposta

import br.com.zup.CriaPropostaRequest
import br.com.zup.PropostaGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import javax.inject.Singleton

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
    fun `Registra nova proposta`() {
        val request = criaPropostaRequest()
        val response = grpcClient.cria(request)

        with(response) {
            val proposta = repository.findByDocumento(request.documento).orElseThrow()
            assertNotNull(this)
            assertEquals(proposta.id.toString(), idProposta)
            assertEquals(proposta.email, request.email)
            assertEquals(proposta.nome, request.nome)
            assertEquals(proposta.endereco, request.endereco)
            assertEquals(proposta.salario.toDouble(), request.salario)
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource(strings = ["cpfInvalido"])
    fun `Retorna INVALID_ARGUMENT quando cpf eh vazio, nulo ou invalido`(cpf: String?) {
        val request = criaPropostaRequest(documento = cpf)
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            cpf?.let {
                assertFalse(repository.existsByDocumento(it))
            }
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource(strings = ["emailInvalido"])
    fun `Retorna INVALID_ARGUMENT quando email eh vazio, nulo ou invalido`(email: String?) {
        val request = criaPropostaRequest(email = email)
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertFalse(repository.existsByDocumento(request.documento))
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    fun `Retorna INVALID_ARGUMENT quando nome eh vazio ou nulo`(nome: String?) {
        val request = criaPropostaRequest(nome = nome)
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertFalse(repository.existsByDocumento(request.documento))
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    fun `Retorna INVALID_ARGUMENT quando endereco eh vazio ou nulo`(endereco: String?) {
        val request = criaPropostaRequest(endereco = endereco)
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertFalse(repository.existsByDocumento(request.documento))
        }
    }

    @Test
    fun `Retorna INVALID_ARGUMENT quando salario eh negativo`() {
        val request = criaPropostaRequest(salario = -0.01)
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertFalse(repository.existsByDocumento(request.documento))
        }
    }

    private fun criaPropostaRequest(
        documento: String? = "324.739.930-50",
        email: String? = "email@test.com",
        nome: String? = "Nome",
        endereco: String? = "Endereco",
        salario: Double? = 2000.00
    ): CriaPropostaRequest {
        return when {
            documento == null -> {
                CriaPropostaRequest.newBuilder()
                    .setEmail(email)
                    .setNome(nome)
                    .setEndereco(endereco)
                    .setSalario(salario!!)
                    .build()
            }
            email == null -> {
                CriaPropostaRequest.newBuilder()
                    .setDocumento(documento)
                    .setNome(nome)
                    .setEndereco(endereco)
                    .setSalario(salario!!)
                    .build()
            }
            nome == null -> {
                CriaPropostaRequest.newBuilder()
                    .setDocumento(documento)
                    .setEmail(email)
                    .setEndereco(endereco)
                    .setSalario(salario!!)
                    .build()
            }
            endereco == null -> {
                CriaPropostaRequest.newBuilder()
                    .setDocumento(documento)
                    .setEmail(email)
                    .setNome(nome)
                    .setSalario(salario!!)
                    .build()
            }
            salario == null -> {
                CriaPropostaRequest.newBuilder()
                    .setDocumento(documento)
                    .setEmail(email)
                    .setNome(nome)
                    .setEndereco(endereco)
                    .build()
            }
            else -> {
                CriaPropostaRequest.newBuilder()
                    .setDocumento(documento)
                    .setEmail(email)
                    .setNome(nome)
                    .setEndereco(endereco)
                    .setSalario(salario)
                    .build()
            }
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                PropostaGrpcServiceGrpc.PropostaGrpcServiceBlockingStub {
            return PropostaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
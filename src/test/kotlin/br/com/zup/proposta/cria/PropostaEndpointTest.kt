package br.com.zup.proposta.cria

import br.com.zup.CriaPropostaRequest
import br.com.zup.PropostaGrpcServiceGrpc
import br.com.zup.analise.HttpClientAnaliseFinanceira
import br.com.zup.analise.ResultadoAnalise
import br.com.zup.analise.SolicitacaoAnaliseRequest
import br.com.zup.analise.SolicitacaoAnaliseResponse
import br.com.zup.proposta.Proposta
import br.com.zup.proposta.PropostaRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class PropostaEndpointTest(
    private val repository: PropostaRepository,
    private val grpcClient: PropostaGrpcServiceGrpc.PropostaGrpcServiceBlockingStub
) {

    @Inject
    lateinit var analiseClient: HttpClientAnaliseFinanceira

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `Registra nova proposta elegivel quando api analise retorna status 200`() {
        val request = criaPropostaRequest()
        `when`(analiseClient.solicitacaoAnalise(solicitacaoAnaliseRequest(request)))
            .thenReturn(HttpResponse.ok(solicitacaoAnaliseResponse(request, ResultadoAnalise.SEM_RESTRICAO)))

        val response = grpcClient.cria(request)

        with(response) {
            val proposta = repository.findByDocumento(request.documento).orElseThrow()
            assertNotNull(this)
            assertEquals(proposta.id.toString(), idProposta)
            assertEquals(proposta.email, request.email)
            assertEquals(proposta.nome, request.nome)
            assertEquals(proposta.endereco, request.endereco)
            assertEquals(proposta.salario.toDouble(), request.salario)
            assertTrue(proposta.propostaElegivel)
        }
    }

    @Test
    fun `Registra nova proposta nao elegivel quando api analise retorna status 422`() {
        val request = criaPropostaRequest()
        `when`(analiseClient.solicitacaoAnalise(solicitacaoAnaliseRequest(request)))
            .thenReturn(HttpResponse.ok(solicitacaoAnaliseResponse(request, ResultadoAnalise.COM_RESTRICAO)))

        val response = grpcClient.cria(request)

        with(response) {
            val proposta = repository.findByDocumento(request.documento).orElseThrow()
            assertNotNull(this)
            assertEquals(proposta.id.toString(), idProposta)
            assertEquals(proposta.email, request.email)
            assertEquals(proposta.nome, request.nome)
            assertEquals(proposta.endereco, request.endereco)
            assertEquals(proposta.salario.toDouble(), request.salario)
            assertFalse(proposta.propostaElegivel)
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

    @Test
    fun `Retorna INVALID_ARGUMENT quando ja existe uma proposta para o documento`() {
        val request = criaPropostaRequest()
        repository.save(
            Proposta(
                documento = request.documento,
                email = request.email,
                nome = request.nome,
                endereco = request.endereco,
                salario = BigDecimal.valueOf(request.salario)
            )
        )

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(request)
        }

        with(response) {
            assertEquals(status.code, Status.INVALID_ARGUMENT.code)
            assertTrue(message!!.contains("Ja existe uma proposta para este cliente"))
            assertEquals(repository.findAll().size, 1)
        }
    }

    private fun solicitacaoAnaliseRequest(request: CriaPropostaRequest) =
        SolicitacaoAnaliseRequest(request.documento, request.nome, "request.id")

    private fun solicitacaoAnaliseResponse(request: CriaPropostaRequest, resultadoAnalise: ResultadoAnalise) =
        SolicitacaoAnaliseResponse(request.documento, request.nome, "request.id", resultadoAnalise)

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

    @MockBean(HttpClientAnaliseFinanceira::class)
    fun analiseClient(): HttpClientAnaliseFinanceira {
        return Mockito.mock(HttpClientAnaliseFinanceira::class.java)
    }
}
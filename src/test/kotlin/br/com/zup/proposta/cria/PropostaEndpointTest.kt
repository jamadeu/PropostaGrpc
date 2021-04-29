package br.com.zup.proposta.cria

import br.com.zup.CriaPropostaRequest
import br.com.zup.PropostaGrpcServiceGrpc
import br.com.zup.analise.HttpClientAnaliseFinanceira
import br.com.zup.analise.ResultadoAnalise
import br.com.zup.analise.SolicitacaoAnaliseRequest
import br.com.zup.analise.SolicitacaoAnaliseResponse
import br.com.zup.proposta.Proposta
import br.com.zup.proposta.PropostaRepository
import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    private val criaPropostaRequest: CriaPropostaRequest =
        CriaPropostaRequest.newBuilder()
            .setDocumento("324.739.930-50")
            .setEmail("email@test.com")
            .setNome("Nome")
            .setEndereco("Endereco")
            .setSalario(2000.00)
            .build()

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `Registra nova proposta elegivel quando api analise retorna status 200`() {
        val request = criaPropostaRequest
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
        val request = criaPropostaRequest
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

    @Test
    fun `Retorna INVALID_ARGUMENT quando ja existe uma proposta para o documento`() {
        val request = criaPropostaRequest
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

    @Test
    fun `Retorna INVALID_ARGUMENT quando dados invalidos`() {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cria(CriaPropostaRequest.newBuilder().setSalario(-0.1).build())
        }

        with(response) {
            val details = StatusProto.fromThrowable(this)?.detailsList?.get(0)!!.unpack(BadRequest::class.java)
            val violations = details.fieldViolationsList.map { it.field to it.description }

            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("request with invalid parameters", status.description)
            assertTrue(violations.contains(Pair("nome", "não deve estar em branco")))
            assertTrue(violations.contains(Pair("documento", "não deve estar em branco")))
            assertTrue(violations.contains(Pair("documento", "não é um documento valido")))
            assertTrue(violations.contains(Pair("salario", "deve ser maior ou igual a 0")))
            assertTrue(violations.contains(Pair("endereco", "não deve estar em branco")))
            assertTrue(violations.contains(Pair("email", "não deve estar em branco")))
            assertEquals(6, violations.size)
        }
    }

    private fun solicitacaoAnaliseRequest(request: CriaPropostaRequest) =
        SolicitacaoAnaliseRequest(request.documento, request.nome, "request.id")

    private fun solicitacaoAnaliseResponse(request: CriaPropostaRequest, resultadoAnalise: ResultadoAnalise) =
        SolicitacaoAnaliseResponse(request.documento, request.nome, "request.id", resultadoAnalise)


    @MockBean(HttpClientAnaliseFinanceira::class)
    fun analiseClient(): HttpClientAnaliseFinanceira {
        return Mockito.mock(HttpClientAnaliseFinanceira::class.java)
    }
}
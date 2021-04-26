package br.com.zup.proposta

import br.com.zup.*
import br.com.zup.compartilhado.excecoes.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class PropostaEndpoint(
    @Inject private val propostaService: PropostaService
) : PropostaGrpcServiceGrpc.PropostaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cria(request: CriaPropostaRequest, responseObserver: StreamObserver<CriaPropostaResponse>) {
        logger.info("Request nova proposta: $request")

        val proposta = propostaService.criaProposta(request.toModel())
            .also { logger.info("Proposta criada $it") }

        responseObserver.onNext(
            CriaPropostaResponse.newBuilder()
                .setIdProposta("${proposta.id}")
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun consulta(
        request: ConsultaPropostaRequest,
        responseObserver: StreamObserver<ConsultaPropostaResponse>
    ) {
        logger.info("Consulta proposta id ${request.idProposta}")

        val response =
            propostaService.consulta(
                UUID.fromString(
                    request.idProposta ?: throw IllegalArgumentException("Id proposta nao pode ser nulo")
                )
            ).let { proposta ->
                ConsultaPropostaResponse.newBuilder()
                    .setIdProposta(proposta.id.toString())
                    .setNome(proposta.nome)
                    .setDocumento(proposta.documento)
                    .setEndereco(proposta.endereco)
                    .setSalario(proposta.salario.toDouble())
                    .setStatus(proposta.statusProposta)
                    .setElegivel(proposta.propostaElegivel)
                    .setCartao(proposta.cartao?.numero ?: "Nao possui cartao")
                    .build()
            }.also {
                logger.info("Detalhes da proposta $it")
            }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
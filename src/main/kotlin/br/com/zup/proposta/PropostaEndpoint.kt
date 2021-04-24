package br.com.zup.proposta

import br.com.zup.CriaPropostaRequest
import br.com.zup.CriaPropostaResponse
import br.com.zup.PropostaGrpcServiceGrpc
import br.com.zup.compartilhado.excecoes.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class PropostaEndpoint(
    @Inject private val novaPropostaService: NovaPropostaService
) : PropostaGrpcServiceGrpc.PropostaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cria(request: CriaPropostaRequest, responseObserver: StreamObserver<CriaPropostaResponse>) {
        logger.info("Request nova proposta: $request")

        val proposta = novaPropostaService.criaProposta(request.toModel())
            .also { logger.info("Proposta criada $it") }

        responseObserver.onNext(
            CriaPropostaResponse.newBuilder()
                .setIdProposta("${proposta.id}")
                .build()
        )
        responseObserver.onCompleted()
    }
}
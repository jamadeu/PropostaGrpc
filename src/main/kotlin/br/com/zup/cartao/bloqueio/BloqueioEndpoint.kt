package br.com.zup.cartao.bloqueio

import br.com.zup.BloqueiaCartaoRequest
import br.com.zup.BloqueiaCartaoResponse
import br.com.zup.CartaoGrpcServiceGrpc
import br.com.zup.cartao.CartaoRepository
import br.com.zup.compartilhado.excecoes.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class BloqueioEndpoint(
    @Inject val cartaoRepository: CartaoRepository
) : CartaoGrpcServiceGrpc.CartaoGrpcServiceImplBase() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun bloqueia(request: BloqueiaCartaoRequest,
                          responseObserver: StreamObserver<BloqueiaCartaoResponse>) {
        logger.info("Bloqueio request $request")



    }
}
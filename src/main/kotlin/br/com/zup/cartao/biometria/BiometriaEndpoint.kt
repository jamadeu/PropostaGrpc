package br.com.zup.cartao.biometria

import br.com.zup.BiometriaGrpcServiceGrpc
import br.com.zup.CadastraBiometriaRequest
import br.com.zup.CadastraBiometriaResponse
import br.com.zup.compartilhado.excecoes.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class BiometriaEndpoint(
    @Inject private val biometriaService: BiometriaService
) : BiometriaGrpcServiceGrpc.BiometriaGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastra(
        request: CadastraBiometriaRequest,
        responseObserver: StreamObserver<CadastraBiometriaResponse>
    ) {
        logger.info("Request cadastra biometria $request")
        if(request.biometria.isNullOrBlank()){
            logger.error("Biometria invalida ${request.biometria}")
            throw IllegalArgumentException("Biometria nao pode ser nula ou vazia")
        }
        if(request.idCartao.isNullOrBlank()){
            logger.error("Id cartao invalido ${request.biometria}")
            throw IllegalArgumentException("Id cartao nao pode ser nulo ou vazio")
        }

        val biometriaEncoded = Base64.getEncoder().encodeToString(request.biometria.toByteArray())
        logger.info("Biometria encoded $biometriaEncoded")

        val cartao = biometriaService.cadastra(
            UUID.fromString(request.idCartao),
            biometriaEncoded
                .also { logger.info("Biometria $it") }
        )

        logger.info("Cartao atualizado $cartao")
        val biometria = cartao.biometrias.single { b ->
            b.biometria == biometriaEncoded
        }
        val response =
            CadastraBiometriaResponse.newBuilder()
                .setIdBiometria(biometria.id.toString())
                .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
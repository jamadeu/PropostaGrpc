package br.com.zup

import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class PropostaEndpoint: PropostaGrpcServiceGrpc.PropostaGrpcServiceImplBase() {

    override fun cria(request: CriaPropostaRequest, responseObserver: StreamObserver<CriaPropostaResponse>) {

    }
}
package br.com.zup.proposta.clientFactory

import br.com.zup.PropostaGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import javax.inject.Singleton

@Factory
class Clients {
    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
            PropostaGrpcServiceGrpc.PropostaGrpcServiceBlockingStub {
        return PropostaGrpcServiceGrpc.newBlockingStub(channel)
    }
}
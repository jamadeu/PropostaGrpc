package br.com.zup.cartao

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("\${cartoes.api}")
interface HttpClientCartao {

    @Post("/api/cartoes")
    fun solicitaCartao(@Body request: SolicitaCartaoRequest): HttpResponse<SolicitaCartaoResponse>
}
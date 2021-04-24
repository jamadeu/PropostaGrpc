package br.com.zup.analise

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Fallback
import io.micronaut.retry.annotation.Retryable

@Client("\${analise.financeira.api}")
interface HttpClientAnaliseFinanceira {

    @Post
    fun solicitacaoAnalise(@Body request: SolicitacaoAnaliseRequest): HttpResponse<SolicitacaoAnaliseResponse>
}

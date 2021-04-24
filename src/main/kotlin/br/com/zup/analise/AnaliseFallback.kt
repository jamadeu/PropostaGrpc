package br.com.zup.analise

import io.micronaut.http.HttpResponse
import io.micronaut.retry.annotation.Fallback

@Fallback
class AnaliseFallback : HttpClientAnaliseFinanceira {
    override fun solicitacaoAnalise(request: SolicitacaoAnaliseRequest): HttpResponse<SolicitacaoAnaliseResponse> {
        return HttpResponse.ok(
            SolicitacaoAnaliseResponse(
                documento = request.documento,
                nome = request.nome,
                idProposta = request.idProposta,
                resultadoAnalise = ResultadoAnalise.COM_RESTRICAO
            )
        )
    }
}
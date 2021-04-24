package br.com.zup.analise

import com.fasterxml.jackson.annotation.JsonProperty

data class SolicitacaoAnaliseResponse (
    val documento: String?,
    val nome: String?,
    val idProposta: String?,
    @JsonProperty("resultadoSolicitacao") val resultadoAnalise: ResultadoAnalise?
)

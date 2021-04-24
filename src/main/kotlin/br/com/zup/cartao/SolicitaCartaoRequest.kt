package br.com.zup.cartao

data class SolicitaCartaoRequest(
    val documento: String,
    val nome: String,
    val idProposta: String
)

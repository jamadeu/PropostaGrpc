package br.com.zup.cartao.clienteHttp

data class SolicitaCartaoRequest(
    val documento: String,
    val nome: String,
    val idProposta: String
)

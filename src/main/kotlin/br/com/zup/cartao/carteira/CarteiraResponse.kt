package br.com.zup.cartao.carteira

import java.time.LocalDateTime

data class CarteiraResponse(
    val idCarteira: String?,
    val email: String?,
    val associadaEm: LocalDateTime?,
    val emissor: String?,
) {
    fun toModel() = Carteira(
        idCarteira ?: throw IllegalStateException("Id carteira nulo"),
        email ?: throw IllegalStateException("Email carteira nulo"),
        associadaEm ?: throw IllegalStateException("Data em que a carteira foi associada nula"),
        emissor ?: throw IllegalStateException("Emissor carteira nulo"),
    )
}
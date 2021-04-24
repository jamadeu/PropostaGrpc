package br.com.zup.cartao.vencimento

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VencimentoResponse(
    @JsonProperty("id") val idVencimento: String?,
    val dia: Int?,
    val dataCriacao: LocalDateTime?
) {
    fun toModel() = Vencimento(
        idVencimento ?: throw IllegalStateException("Id vencimento nulo"),
        dia ?: throw IllegalStateException("dia vencimento nulo"),
        dataCriacao ?: throw IllegalStateException("dataCriacao vencimento nula")
    )
}

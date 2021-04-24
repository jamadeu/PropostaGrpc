package br.com.zup.cartao.renegociacao

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class RenegociacaoResponse(
    @JsonProperty("id") val idRenegociacao: String?,
    val quantidade: Int?,
    val valor: BigDecimal?,
    val dataCriacao: LocalDateTime?
) {
    fun toModel() = Renegociacao(
        idRenegociacao ?: throw IllegalStateException("Id renegociacao nulo"),
        quantidade ?: throw IllegalStateException("quantidade renegociacao nulo"),
        valor ?: throw IllegalStateException("Valor renegociacao nulo"),
        dataCriacao ?: throw IllegalStateException("DataCriacao renegociacao nulo")
    )
}
package br.com.zup.cartao.parcela

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal


data class ParcelaResponse(
    @JsonProperty("id") val idParcela: String?,
    val quantidade: Int?,
    val valor: BigDecimal?,
) {
    fun toModel() = Parcela(
        idParcela ?: throw IllegalStateException("Id parcela nulo"),
        quantidade ?: throw IllegalStateException("Quantidade parcela nulo"),
        valor ?: throw IllegalStateException("Valor parcela nulo")
    )
}
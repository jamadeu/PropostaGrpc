package br.com.zup.cartao

import br.com.zup.cartao.renegociacao.RenegociacaoResponse
import br.com.zup.cartao.vencimento.VencimentoResponse
import br.com.zup.proposta.Proposta
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class SolicitaCartaoResponse(
    @JsonProperty("id") val numeroCartao: String?,
    val titular: String?,
    val emitidoEm: LocalDateTime?,
    val limite: Double?,
    val vencimento: VencimentoResponse?,
    val renegociacao: RenegociacaoResponse?,
    val idProposta: String?
) {
    fun toModel(proposta: Proposta): Cartao {
        if (!idProposta.equals(proposta.id.toString())) {
            throw IllegalArgumentException("Proposta Invalida")
        }

        return Cartao(
            numeroCartao ?: throw IllegalStateException("Numero do cartao nulo"),
            titular ?: throw IllegalStateException("titular nulo"),
            BigDecimal.valueOf(limite ?: throw IllegalStateException("Limite nulo")),
            proposta,
            emitidoEm ?: throw IllegalStateException("Data de emissao nula"),
            vencimento?.toModel() ?: throw IllegalStateException("Vencimento nulo"),
            renegociacao?.toModel()
        )
    }
}

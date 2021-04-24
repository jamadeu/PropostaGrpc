package br.com.zup.cartao.aviso

import java.time.LocalDate

data class AvisoResponse(
    val validoAte: LocalDate?,
    val destino: String?,
) {
    fun toModel() = Aviso(
        validoAte ?: throw IllegalStateException("Data de validade do aviso nula"),
        destino ?: throw IllegalStateException("Destino do aviso nula")
    )
}
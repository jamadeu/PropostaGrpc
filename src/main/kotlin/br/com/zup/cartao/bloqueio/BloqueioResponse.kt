package br.com.zup.cartao.bloqueio

import java.time.LocalDateTime

data class BloqueioResponse(
    val idBloqueio: String?,
    val bloqueadoEm: LocalDateTime?,
    val sistemaResponsavel: String?,
    val ativo: Boolean?
) {
    fun toModel() = Bloqueio(
        idBloqueio ?: throw IllegalStateException("Id bloqueio nulo"),
        bloqueadoEm ?: throw IllegalStateException("Data bloqueio nula"),
        sistemaResponsavel ?: throw IllegalStateException("Sistema Responsavel pelo bloqueio nulo"),
        ativo ?: throw IllegalStateException("Status bloqueio nulo"),
    )
}
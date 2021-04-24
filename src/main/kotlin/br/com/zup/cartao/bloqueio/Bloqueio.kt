package br.com.zup.cartao.bloqueio

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past

@Entity
class Bloqueio(
    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val idBloqueio: String,

    @field:NotNull
    @field:Past
    @Column(nullable = false, updatable = false)
    val bloqueadoEm: LocalDateTime,

    @field:NotBlank
    @Column(nullable = false, updatable = false)
    val sistemaResponsavel: String,

    @field:NotNull
    @Column(nullable = false, updatable = false)
    val ativo: Boolean = false
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}
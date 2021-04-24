package br.com.zup.cartao.renegociacao

import java.math.BigDecimal
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
class Renegociacao(
    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val idRenegociacao: String,

    @field:NotNull
    @Column(nullable = false, updatable = false)
    val quantidade: Int,

    @field:NotNull
    @Column(nullable = false, updatable = false)
    val valor: BigDecimal,

    @field:NotNull
    @field:Past
    @Column(nullable = false)
    val dataCriacao: LocalDateTime
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}
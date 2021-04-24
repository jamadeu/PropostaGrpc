package br.com.zup.cartao.carteira

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
class Carteira(
    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val idCarteira: String,

    @field:NotBlank
    @Column(nullable = false, unique = true)
    val email: String,

    @field:NotNull
    @field:Past
    @Column(nullable = false)
    val associadaEm: LocalDateTime,

    @field:NotBlank
    @Column(nullable = false, updatable = true)
    val emissor: String,
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}
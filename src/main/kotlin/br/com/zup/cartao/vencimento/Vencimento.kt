package br.com.zup.cartao.vencimento

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
class Vencimento(
    @field:NotBlank
    @Column(unique = true, nullable = false, updatable = false)
    val idVencimento: String,

    @field:NotNull
    @Column(nullable = false)
    val dia: Int,

    @field:NotNull
    @field:Past
    @Column(nullable = false)
    val dataCriacao: LocalDateTime

) {

    @Id
    @GeneratedValue
    val id: UUID? = null
}


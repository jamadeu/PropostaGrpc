package br.com.zup.cartao.aviso

import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Future
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Aviso(
    @field:NotNull
    @field:Future
    @Column(nullable = false)
    val validoAte: LocalDate,

    @field:NotBlank
    @Column(nullable = false, updatable = true)
    val destino: String,
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}
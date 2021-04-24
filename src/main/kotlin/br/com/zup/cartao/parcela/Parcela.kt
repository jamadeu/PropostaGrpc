package br.com.zup.cartao.parcela

import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Parcela(
    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val idParcela: String,

    @field:NotNull
    @Column(nullable = false, updatable = false)
    val quantidade: Int,

    @field:NotNull
    @Column(nullable = false, updatable = false)
    val valor: BigDecimal,

) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}
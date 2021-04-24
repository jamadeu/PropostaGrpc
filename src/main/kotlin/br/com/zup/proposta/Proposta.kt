package br.com.zup.proposta

import br.com.zup.compartilhado.anotacoes.CpfCnpj
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class Proposta(
    @field:NotBlank
    @CpfCnpj
    @Column(unique = true, nullable = false, updatable = false)
    val documento: String,

    @field:NotBlank
    @field:Email
    @Column(unique = true, nullable = false)
    val email: String,

    @field:NotBlank
    @Column(nullable = false)
    val nome: String,

    @field:NotBlank
    @Column(nullable = false)
    val endereco: String,

    @field:NotNull
    @field:PositiveOrZero
    @Column(nullable = false)
    val salario: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false, updatable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}
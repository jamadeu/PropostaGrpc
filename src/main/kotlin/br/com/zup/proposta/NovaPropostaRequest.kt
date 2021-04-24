package br.com.zup.proposta

import br.com.zup.compartilhado.anotacoes.CpfCnpj
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Introspected
data class NovaPropostaRequest(
    @field:NotBlank
    @field:CpfCnpj
    val documento: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val nome: String,

    @field:NotBlank
    val endereco: String,

    @field:NotNull
    @field:PositiveOrZero
    val salario: BigDecimal
) {
    fun toModel() = Proposta(documento, email, nome, endereco, salario)
}
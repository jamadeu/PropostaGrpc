package br.com.zup.cartao.bloqueio

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class BloqueioRequest(
    @field:NotBlank val idCartao: String,
    @field:NotBlank val ipCliente: String,
    @field:NotBlank val userAgent: String,
    @field:NotBlank val titular: String,
)
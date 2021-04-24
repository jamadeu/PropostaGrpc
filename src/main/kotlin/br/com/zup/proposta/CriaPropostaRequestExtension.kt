package br.com.zup.proposta

import br.com.zup.CriaPropostaRequest
import java.math.BigDecimal

fun CriaPropostaRequest.toModel(): NovaPropostaRequest {
    return NovaPropostaRequest(
        documento,
        email,
        nome,
        endereco,
        salario = BigDecimal.valueOf(salario)
    )
}


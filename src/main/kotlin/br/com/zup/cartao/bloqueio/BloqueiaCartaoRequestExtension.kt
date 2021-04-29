package br.com.zup.cartao.bloqueio

import br.com.zup.BloqueiaCartaoRequest

fun BloqueiaCartaoRequest.toDto() =
    BloqueioRequest(
        idCartao,
        ipCliente,
        userAgent,
        titular
    )
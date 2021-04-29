package br.com.zup.cartao.bloqueio

import br.com.zup.cartao.CartaoRepository
import br.com.zup.cartao.clienteHttp.HttpClientCartao
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class BloqueioService(
    @Inject val cartaoRepository: CartaoRepository,
    @Inject val httpClientCartao: HttpClientCartao
) {

    @Transactional
    fun bloqueiaCartao(@Valid request: BloqueioRequest){
        cartaoRepository.findById(UUID.fromString(request.idCartao))
    }
}
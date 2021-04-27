package br.com.zup.cartao.biometria

import br.com.zup.cartao.Cartao
import br.com.zup.cartao.CartaoRepository
import br.com.zup.compartilhado.excecoes.NaoLocalizadoException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Validated
@Singleton
class BiometriaService(
    @Inject private val cartaoRepository: CartaoRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(idCartao: UUID, biometria: String): Cartao {
        logger.info("Cadastra biometria")

        val cartao = cartaoRepository.findById(idCartao)
            .orElseThrow {
                throw NaoLocalizadoException("Cartao nao localizado")
            }.apply {
                adicionaBiometria(Biometria(biometria))
            }


        return cartaoRepository.update(cartao)
            .also {
                logger.info("Adicionada biometria $biometria ao cartao ${it.id}")
            }
    }
}

package br.com.zup.cartao.job

import br.com.zup.cartao.clienteHttp.HttpClientCartao
import br.com.zup.cartao.clienteHttp.SolicitaCartaoRequest
import br.com.zup.proposta.PropostaRepository
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class SolicitaCartaoScheduler(
    @Inject val propostaRepository: PropostaRepository,
    @Inject val httpClientCartao: HttpClientCartao
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = "\${scheduler.delay}")
    @Transactional
    open fun solicitaCartao() {
        propostaRepository.findAllPendenteCartao()
            .also { logger.info("Propostas pendentes cartao $it") }
            .forEach { proposta ->
                logger.info("Solicitando cartao para proposta $proposta")
                httpClientCartao
                    .solicitaCartao(
                        SolicitaCartaoRequest(
                            documento = proposta.documento,
                            nome = proposta.nome,
                            idProposta = proposta.id.toString()
                        )
                    )
                    .also { logger.info("HttpClientCartao response $it") }
                    .body()?.let { response ->
                        logger.info("SolicitaCartaoResponse $response")
                        proposta.adicionaCartao(response.toModel(proposta))
                        propostaRepository.update(proposta)
                        logger.info("Cartao ${proposta.cartao} criado")
                        logger.info("Proposta $proposta atualizada")
                    }
            }
    }
}

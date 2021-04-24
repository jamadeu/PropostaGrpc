package br.com.zup.proposta

import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaPropostaService(
    @Inject val repository: PropostaRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun criaProposta(@Valid request: NovaPropostaRequest): Proposta {
        logger.info("NovaPropostaRequest $request")

        return repository.save(request.toModel()).also { logger.info("Proposta criada $it") }
    }
}
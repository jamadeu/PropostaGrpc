package br.com.zup.proposta

import br.com.zup.compartilhado.excecoes.PropostaJaExisteException
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

        if(repository.existsByDocumento(request.documento)){
            logger.error("Ja existe uma proposta para o documento ${request.documento}")
            throw PropostaJaExisteException("Ja existe uma proposta para este cliente")
        }

        return repository.save(request.toModel()).also { logger.info("Proposta criada $it") }
    }
}
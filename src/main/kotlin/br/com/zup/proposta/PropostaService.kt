package br.com.zup.proposta

import br.com.zup.analise.HttpClientAnaliseFinanceira
import br.com.zup.compartilhado.excecoes.NaoLocalizadoException
import br.com.zup.compartilhado.excecoes.PropostaJaExisteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class PropostaService(
    @Inject val repository: PropostaRepository,
    @Inject val analiseClient: HttpClientAnaliseFinanceira
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun criaProposta(@Valid request: NovaPropostaRequest): Proposta {
        logger.info("NovaPropostaRequest $request")

        if (repository.existsByDocumento(request.documento)) {
            logger.error("Ja existe uma proposta para o documento ${request.documento}")
            throw PropostaJaExisteException("Ja existe uma proposta para este cliente")
        }

        val proposta = repository.save(request.toModel())
            .also { logger.info("Proposta criada $it") }

        logger.info("Analise financeira")
        val responseAnalise =
            analiseClient.solicitacaoAnalise(proposta.solicitaAnalise())
                .also { logger.info("Status retorno analise ${it.status}") }
                .body()?.resultadoAnalise

        proposta.resultadoAnalise(responseAnalise ?: throw IllegalStateException("Analise invalida"))

        return proposta
    }

    fun consulta(id: UUID): Proposta {
        logger.info("Consulta proposta $id")
        return repository.findById(id)
            .orElseThrow { throw NaoLocalizadoException("Proposta nao localizada") }
    }
}
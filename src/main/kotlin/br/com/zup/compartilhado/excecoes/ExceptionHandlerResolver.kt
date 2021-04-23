package br.com.zup.compartilhado.excecoes

import br.com.zup.compartilhado.excecoes.handlers.DefaultHandler
import br.com.zup.compartilhado.excecoes.handlers.ExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(
    @Inject private val handlers: List<ExceptionHandler<*>>
) {
    private var defaultHandler: ExceptionHandler<Exception> = DefaultHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) :
            this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<*> {
        val foundHandlers = handlers.filter { it.supports(e) }

        if (foundHandlers.size > 1) {
            throw IllegalStateException("Encontrado mais de 1 handler para a excecao ${e.javaClass.name}: $foundHandlers ")
        }

        return foundHandlers.firstOrNull() ?: defaultHandler
    }
}
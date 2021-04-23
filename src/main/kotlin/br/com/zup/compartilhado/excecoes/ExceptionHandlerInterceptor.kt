package br.com.zup.compartilhado.excecoes

import br.com.zup.compartilhado.excecoes.handlers.ExceptionHandler
import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerInterceptor(
    @Inject private val resolver: ExceptionHandlerResolver
) : MethodInterceptor<BindableService, Any?> {

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        return try {
            context.proceed()
        } catch (e: Exception) {
            @Suppress("UNCHECKED_CAST")
            val handler = resolver.resolve(e) as ExceptionHandler<Exception>
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response().onError(status.asRunTimeException())

            return null
        }
    }

    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any?>) {
        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }

}

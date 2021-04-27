package br.com.zup.compartilhado.excecoes

import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        try {
            return context.proceed()
        } catch (e: Exception) {
            logger.error(e.message)

            val statusError = when (e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                is ConstraintViolationException -> handleConstraintValidationException(e)
                is PropostaJaExisteException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is NaoLocalizadoException -> Status.NOT_FOUND.withDescription(e.message).asRuntimeException()
                else -> Status.UNKNOWN.withDescription("unexpected error happened").asRuntimeException()
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(statusError)
            return null
        }
    }

    private fun handleConstraintValidationException(e: ConstraintViolationException): StatusRuntimeException {
        val badRequest = BadRequest.newBuilder() // com.google.rpc.BadRequest
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name) // propertyPath=save.entity.email
                    .setDescription(it.message)
                    .build()
            }
            ).build()

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("request with invalid parameters")
            .addDetails(com.google.protobuf.Any.pack(badRequest)) // com.google.protobuf.Any
            .build()

        logger.info("$statusProto")
        return StatusProto.toStatusRuntimeException(statusProto) // io.grpc.protobuf.StatusProto
    }

}

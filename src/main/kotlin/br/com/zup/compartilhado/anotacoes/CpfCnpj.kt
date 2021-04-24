package br.com.zup.compartilhado.anotacoes

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@CPF
@CNPJ
@ConstraintComposition(CompositionType.OR)
@ReportAsSingleViolation
@Constraint(validatedBy = [])
@MustBeDocumented
@Target(FIELD, PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CpfCnpj(
    val message: String = "não é um documento valido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

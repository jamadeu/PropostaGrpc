package br.com.zup.compartilhado.anotacoes

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import kotlin.reflect.KClass


@ReportAsSingleViolation
@Constraint(validatedBy = [])
@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class UUID(
    val message: String = "não é UUID valido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

package br.com.zup.compartilhado.anotacoes

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import javax.validation.Payload
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(PROPERTY, CONSTRUCTOR, FIELD, VALUE_PARAMETER )
@CPF
@CNPJ
@ConstraintComposition(CompositionType.OR)
annotation class CpfCnpj(
    val message: String = "não é um documento valido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

package br.com.zup.compartilhado.excecoes

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, FUNCTION)
@Around
annotation class ErrorHandler()

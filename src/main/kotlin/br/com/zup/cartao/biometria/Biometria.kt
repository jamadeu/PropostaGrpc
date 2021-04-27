package br.com.zup.cartao.biometria

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Biometria(
    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val biometria: String
) {

    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false, updatable = false)
    val associadaEM: LocalDateTime = LocalDateTime.now()
}
package br.com.zup.proposta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PropostaRepository : JpaRepository<Proposta, UUID> {
    fun existsByDocumento(documento: String): Boolean
    fun findByDocumento(documento: String): Optional<Proposta>

}
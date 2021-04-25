package br.com.zup.proposta

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PropostaRepository : JpaRepository<Proposta, UUID> {
    fun existsByDocumento(documento: String): Boolean
    fun findByDocumento(documento: String): Optional<Proposta>

    @Query("SELECT p FROM Proposta p WHERE p.propostaElegivel is TRUE AND p.statusProposta LIKE 'PENDENTE'")
    fun findAllPendenteCartao(): List<Proposta>

}
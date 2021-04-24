package br.com.zup.cartao

import br.com.zup.cartao.aviso.Aviso
import br.com.zup.cartao.bloqueio.Bloqueio
import br.com.zup.cartao.carteira.Carteira
import br.com.zup.cartao.parcela.Parcela
import br.com.zup.cartao.renegociacao.Renegociacao
import br.com.zup.cartao.vencimento.Vencimento
import br.com.zup.proposta.Proposta
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class Cartao(

    @field:NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    val numero: String,

    @field:NotBlank
    @Column(nullable = false, updatable = false)
    val titular: String,

    @field:NotNull
    @PositiveOrZero
    @Column(nullable = false)
    val limite: BigDecimal,

    @field:NotNull
    @OneToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "idProposta", referencedColumnName = "id")
    val proposta: Proposta,

    @field:NotNull
    @Column(updatable = false, nullable = false)
    val emitidoEm: LocalDateTime,

    @OneToOne(cascade = [CascadeType.MERGE])
    val vencimento: Vencimento,

    @OneToOne(cascade = [CascadeType.MERGE])
    val renegociacao: Renegociacao?,

    @OneToMany(cascade = [CascadeType.MERGE])
    val parcelas: Set<Parcela> = mutableSetOf<Parcela>(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val carteiras: Set<Carteira> = mutableSetOf<Carteira>(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val avisos: Set<Aviso> = mutableSetOf<Aviso>(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val bloqueios: Set<Bloqueio> = mutableSetOf<Bloqueio>(),
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}


package br.com.zup.cartao

import br.com.zup.cartao.aviso.Aviso
import br.com.zup.cartao.biometria.Biometria
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

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val vencimento: Vencimento,

    @OneToOne(cascade = [CascadeType.MERGE])
    val renegociacao: Renegociacao?,

    @OneToMany(cascade = [CascadeType.MERGE])
    val parcelas: MutableSet<Parcela> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val carteiras: MutableSet<Carteira> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val avisos: MutableSet<Aviso> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.MERGE])
    val bloqueios: MutableSet<Bloqueio> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    var biometrias: MutableSet<Biometria> = mutableSetOf()
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    var atualizadoEm: LocalDateTime = LocalDateTime.now()

    fun adicionaBiometria(biometria: Biometria) {
        biometrias.add(biometria)
        atualizadoEm = LocalDateTime.now()
    }
}


package br.com.zup.proposta

import br.com.zup.analise.ResultadoAnalise
import br.com.zup.analise.SolicitacaoAnaliseRequest
import br.com.zup.cartao.Cartao
import br.com.zup.compartilhado.anotacoes.CpfCnpj
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class Proposta(
    @field:NotBlank
    @CpfCnpj
    @Column(unique = true, nullable = false, updatable = false)
    val documento: String,

    @field:NotBlank
    @field:Email
    @Column(nullable = false)
    val email: String,

    @field:NotBlank
    @Column(nullable = false)
    val nome: String,

    @field:NotBlank
    @Column(nullable = false)
    val endereco: String,

    @field:NotNull
    @field:PositiveOrZero
    @Column(nullable = false)
    val salario: BigDecimal,

    @Column(nullable = false)
    var propostaElegivel: Boolean = false,

    @Enumerated(EnumType.STRING)
    var statusProposta: StatusProposta = StatusProposta.PENDENTE,

    @OneToOne(mappedBy = "proposta", cascade = [CascadeType.ALL])
    var cartao: Cartao? = null
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false, updatable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var atualizadaEm: LocalDateTime = LocalDateTime.now()

    fun resultadoAnalise(resultado: ResultadoAnalise) {
        if (resultado == ResultadoAnalise.COM_RESTRICAO) {
            propostaElegivel = false
            concluiProposta()
        } else {
            propostaElegivel = true
            atualizadaEm = LocalDateTime.now()
        }
    }

    fun solicitaAnalise() = SolicitacaoAnaliseRequest(
        documento, nome, idProposta = id.toString()
    )

    private fun concluiProposta() {
        statusProposta = StatusProposta.CONCLUIDA
        atualizadaEm = LocalDateTime.now()
    }

    fun adicionaCartao(cartao: Cartao){
        this.cartao = cartao
        concluiProposta()
    }

}
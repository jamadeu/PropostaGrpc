package br.com.zup.analise

data class SolicitacaoAnaliseRequest(
    val documento: String,
    val nome: String,
    val idProposta: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolicitacaoAnaliseRequest

        if (documento != other.documento) return false
        if (nome != other.nome) return false

        return true
    }

    override fun hashCode(): Int {
        var result = documento.hashCode()
        result = 31 * result + nome.hashCode()
        return result
    }
}

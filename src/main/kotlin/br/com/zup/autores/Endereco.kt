package br.com.zup.autores

import javax.persistence.Embeddable

@Embeddable
class Endereco(enderecoResponse: EnderecoResponse,
               val cep: String,
               val numero: String) {

    val logradouro = enderecoResponse.logradouro
    val complemento = enderecoResponse.complemento
    val bairro = enderecoResponse.bairro
    val localidade = enderecoResponse.localidade
    val uf = enderecoResponse.uf

}

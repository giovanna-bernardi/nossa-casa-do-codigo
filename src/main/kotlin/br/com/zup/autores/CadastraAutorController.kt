package br.com.zup.autores

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/autores")
class CadastraAutorController(
    val autorRepository: AutorRepository,
    val enderecoClient: EnderecoClient
) {

    @Post
    @Transactional
    fun cadastra(@Body @Valid request: NovoAutorRequest): HttpResponse<Any> {
        println("Requisição => $request")

        // fazer uma requisição para um serviço externo
        val enderecoResponse: HttpResponse<EnderecoResponse> = enderecoClient.consulta(request.cep)

        // certo é checar se o body não é null primeiro
        if(enderecoResponse.body() == null) {
            return HttpResponse.badRequest()
        }

        val autor = request.paraAutor(enderecoResponse.body()!!)

        autorRepository.save(autor)
        println("Autor => ${autor.nome}")

        val uri = UriBuilder.of("/autores/{id}").expand(mutableMapOf(Pair("id", autor.id)))
        return HttpResponse.created(uri)
    }

    // /autores?email=
    @Get
    @Transactional
    fun lista(@QueryValue(defaultValue = "") email: String): HttpResponse<Any> {
        if (email.isBlank()) {
            val autores = autorRepository.findAll()
            val detalhesDosAutores = autores.map { autor -> DetalhesDoAutorResponse(autor) }
            return HttpResponse.ok(detalhesDosAutores)
        }

        val autorOptional = autorRepository.findByEmail(email)
//        val autorOptional = autorRepository.buscaPorEmail(email)

        if (autorOptional.isEmpty) {
            return HttpResponse.notFound()
        }

        val autor = autorOptional.get()
        return HttpResponse.ok(DetalhesDoAutorResponse(autor))

    }

    @Put("/{id}")
    @Transactional
    fun atualiza(@PathVariable id: Long, descricao: String): HttpResponse<DetalhesDoAutorResponse> {
        val autorOptional = autorRepository.findById(id)

        if (autorOptional.isEmpty) {
            return HttpResponse.notFound()
        }

        val autor = autorOptional.get()
        autor.descricao = descricao
        // agora que temos o @Transactional, não precisa, pois ao fazer um select
        // ele está no estado Managed(gerenciado). Toda alteração que ocorra com esse
        // objeto, no final da transação ele é commitado (aplica as alterações (update) no BD).
//        autorRepository.update(autor)

        return HttpResponse.ok(DetalhesDoAutorResponse(autor))
    }

    @Delete("/{id}")
    @Transactional
    fun deleta(@PathVariable id: Long): HttpResponse<Any> {
        val autorOptional = autorRepository.findById(id)

        // Se passar id inexistente, ele volta 200 no delete, então é bom
        // checar primeiro
        if (autorOptional.isEmpty) {
            return HttpResponse.notFound()
        }

        autorRepository.deleteById(id)
        // ou
        // val autor = autorOptional.get()
        // autorRepository.delete(autor)

        return HttpResponse.ok()
    }
}


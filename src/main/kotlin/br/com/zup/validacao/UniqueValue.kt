package br.com.zup.validacao

import br.com.zup.autores.AutorRepository
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Target(FIELD, CONSTRUCTOR)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueValueValidator::class])
annotation class UniqueValue(val message: String = "E-mail deve ser único")

@Singleton
class UniqueValueValidator(val autorRepository: AutorRepository) : ConstraintValidator<UniqueValue, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<UniqueValue>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value == null) {
            return true // já estou verificando se é @NotBlank
        }

        val autorOptional = autorRepository.findByEmail(value)
        return autorOptional.isEmpty
    }

}

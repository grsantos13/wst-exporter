package br.com.gn.shared.validation

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.persistence.EntityManager
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ExistsResourceValidator::class])
annotation class ExistsResource(
    val message: String = "The value does not exist",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val field: String,
    val domainClass: KClass<*>
)

class ExistsResourceValidator(private val manager: EntityManager) : ConstraintValidator<Unique, Any> {
    override fun isValid(
        value: Any?,
        annotationMetadata: AnnotationValue<Unique>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) return true

        val field = annotationMetadata.convertibleValues.getValue("field")
        val clazz = annotationMetadata.convertibleValues.getValue("domainClass")

        val query = manager.createQuery(" select 1 from $clazz where $field = :value ")
        query.setParameter("value", value)

        return query.resultList.isNotEmpty()
    }
}
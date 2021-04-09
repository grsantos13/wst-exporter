package br.com.gn.shared.validation

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

@MustBeDocumented
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueValidator::class])
annotation class Unique(
    val message: String = "The value already exists",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val field: String,
    val domainClass: KClass<*>
)

@Singleton
open class UniqueValidator(private val manager: EntityManager) : ConstraintValidator<Unique, Any> {

    @Transactional
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

        return query.resultList.isEmpty()
    }
}

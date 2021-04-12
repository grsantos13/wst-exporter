package br.com.gn.exporter

import br.com.gn.exporter.register.Register
import br.com.gn.shared.exception.ObjectAlreadyExistsException
import br.com.gn.shared.exception.ObjectNotFoundException
import br.com.gn.shared.validation.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class ExporterService(
    private val repository: ExporterRepository,
    private val manager: EntityManager
) {

    @Transactional
    fun create(@Valid request: NewExporterRequest): Exporter {
        if (repository.existsByCode(request.code))
            throw ObjectAlreadyExistsException("Exporter already exists with code ${request.code}")

        // Remove dos exportadores pendentes de cadastro
        manager.createQuery(" select r from Register r where r.code = :code ", Register::class.java)
            .setParameter("code", request.code)
            .resultList
            .map(manager::remove)

        val exporter = request.toModel()
        repository.save(exporter)
        return exporter
    }

    @Transactional
    fun read(name: String): List<Exporter> {
        return when {
            name.isNullOrBlank() -> repository.findAll()
            else -> repository.findByName(name)
        }
    }

    @Transactional
    fun update(@Valid request: UpdateExporterRequest, @NotBlank @ValidUUID id: String): Exporter {
        val exporter = repository.findById(UUID.fromString(id))
            .orElseThrow { ObjectNotFoundException("Exporter not found with id $id") }

        exporter.update(request)

        return exporter
    }

    @Transactional
    fun delete(@NotBlank @ValidUUID id: String): Exporter {
        val exporter = repository.findById(UUID.fromString(id))
            .orElseThrow { ObjectNotFoundException("Exporter not found with id $id") }

        repository.delete(exporter)
        return exporter
    }

}
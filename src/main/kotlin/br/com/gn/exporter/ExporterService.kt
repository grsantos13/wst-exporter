package br.com.gn.exporter

import br.com.gn.ReadExporterRequest
import br.com.gn.ReadExporterRequest.FilterCase.CODE
import br.com.gn.ReadExporterRequest.FilterCase.NAME
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
    fun read(request: ReadExporterRequest): List<Exporter> {
        this.validateReadExporterRequest(request)
        return when (request.filterCase) {
            CODE -> repository.findByCode(request.code)
            NAME -> repository.findByName(request.name)
            else -> repository.findAll()
        }
    }

    private fun validateReadExporterRequest(request: ReadExporterRequest) {
        when (request.filterCase) {
            CODE -> if (request.code.isNullOrBlank()) throw IllegalArgumentException("Code must be informed")
            NAME -> if (request.name.isNullOrBlank()) throw IllegalArgumentException("Name must be informed")
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
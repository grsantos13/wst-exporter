package br.com.gn.exporter

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ExporterRepository : JpaRepository<Exporter, UUID> {
    fun existsByCode(code: String): Boolean
    fun findByName(name: String): List<Exporter>
}
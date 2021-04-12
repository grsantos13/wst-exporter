package br.com.gn.shared.kafka

import br.com.gn.exporter.ExporterRepository
import br.com.gn.exporter.register.Register
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.Body
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.validation.constraints.NotBlank

@Validated
@Singleton
@KafkaListener(groupId = "exporters")
class Consumer(
    private val manager: EntityManager,
    private val repository: ExporterRepository
) {

    @Topic("exporters")
    fun receive(@KafkaKey code: String, @NotBlank @Body name: String ){
        if (!repository.existsByCode(code))
            manager.persist(Register(code, name))
    }
}
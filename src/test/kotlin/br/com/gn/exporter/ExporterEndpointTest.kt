package br.com.gn.exporter

import br.com.gn.*
import br.com.gn.Currency
import br.com.gn.Incoterm
import br.com.gn.NewExporterRequest
import br.com.gn.PaymentTerms
import br.com.gn.UpdateExporterRequest
import br.com.gn.util.StatusRuntimeExceptionUtils.Companion.violations
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import br.com.gn.address.Address as ExporterAddress
import br.com.gn.exporter.Currency as ExporterCurrency
import br.com.gn.exporter.Incoterm as ExporterIncoterm

@MicronautTest(transactional = false)
internal class ExporterEndpointTest(
    private val grpcClient: ExporterServiceGrpc.ExporterServiceBlockingStub,
    private val repository: ExporterRepository
) {

    @AfterEach
    fun after() {
        repository.deleteAll()
    }

    @Test
    fun `should create an exporter successfully`() {

        val response = grpcClient.create(
            NewExporterRequest.newBuilder()
                .setAddress(
                    Address.newBuilder()
                        .setZipCode("123456789")
                        .setStreet("Avenida Invernada")
                        .setCountry("Brazil")
                        .setCity("Valinhos")
                        .build()
                ).setCode("12345678")
                .setIncoterm(Incoterm.CIF)
                .setPaymentTerms(PaymentTerms.E30)
                .setName("Exporter test")
                .setCurrency(Currency.EUR)
                .setAvailabilityLT(30)
                .setDepartureLT(6)
                .setArrivalLT(20)
                .setTotalLT(80)
                .build()
        )

        assertNotNull(response.id)
    }

    @Test
    fun `should not create an exporter due to existing code`() {

        repository.save(createExporter())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.create(
                NewExporterRequest.newBuilder()
                    .setAddress(
                        Address.newBuilder()
                            .setZipCode("123456789")
                            .setStreet("Avenida Invernada")
                            .setCountry("Brazil")
                            .setCity("Valinhos")
                            .build()
                    ).setCode("12345678")
                    .setIncoterm(Incoterm.CIF)
                    .setPaymentTerms(PaymentTerms.E30)
                    .setName("Exporter test")
                    .setCurrency(Currency.EUR)
                    .setAvailabilityLT(30)
                    .setDepartureLT(6)
                    .setArrivalLT(20)
                    .setTotalLT(80)
                    .build()
            )
        }

        assertEquals(Status.ALREADY_EXISTS.code, exception.status.code)
        assertEquals("Exporter already exists with code 12345678", exception.status.description)
    }

    @Test
    fun `should not create an exporter due to invalid parameters`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.create(
                NewExporterRequest.newBuilder()
                    .build()
            )
        }
        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Arguments validation error", status.description)
            assertThat(
                violations(this), containsInAnyOrder(
                    Pair("name", "must not be blank"),
                    Pair("street", "must not be blank"),
                    Pair("code", "must not be blank"),
                    Pair("paymentTerms", "must not be null"),
                    Pair("city", "must not be blank"),
                    Pair("country", "must not be blank"),
                    Pair("incoterm", "must not be null"),
                    Pair("currency", "must not be null"),
                    Pair("zipCode", "must not be blank")
                )
            )
        }
    }

    @Test
    fun `should read by name`() {
        repository.save(createExporter())
        repository.save(createExporter(name = "NotFound", code = "09876543"))

        val response = grpcClient.read(ReadExporterRequest.newBuilder().setName("Test").build())

        assertEquals(1, response.exportersList.size)
    }

    @Test
    fun `should read all`() {
        repository.save(createExporter())
        repository.save(createExporter("0987654"))

        val response = grpcClient.read(ReadExporterRequest.newBuilder().build())

        assertEquals(2, response.exportersList.size)
    }

    @Test
    fun `should update a exporter successfully`() {
        val exporter = repository.save(createExporter())

        val response = grpcClient.update(
            UpdateExporterRequest.newBuilder()
                .setAddress(
                    Address.newBuilder()
                        .setZipCode("123456789")
                        .setStreet("Avenida Invernada")
                        .setCountry("Brazil")
                        .setCity("Valinhos")
                        .build()
                ).setId(exporter.id.toString())
                .setIncoterm(Incoterm.CIF)
                .setPaymentTerms(PaymentTerms.E30)
                .setName("Exporter test")
                .setCurrency(Currency.EUR)
                .build()
        )

        assertEquals("Exporter test", response.name)
    }

    @Test
    fun `should not update a exporter due to not finding by id`() {

        val randomId = UUID.randomUUID().toString()
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.update(
                UpdateExporterRequest.newBuilder()
                    .setAddress(
                        Address.newBuilder()
                            .setZipCode("123456789")
                            .setStreet("Avenida Invernada")
                            .setCountry("Brazil")
                            .setCity("Valinhos")
                            .build()
                    ).setId(randomId)
                    .setIncoterm(Incoterm.CIF)
                    .setPaymentTerms(PaymentTerms.E30)
                    .setName("Exporter test")
                    .setCurrency(Currency.EUR)
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, exception.status.code)
        assertEquals("Exporter not found with id $randomId", exception.status.description)
    }

    @Test
    fun `should not update an exporter due to invalid parameters`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.update(
                UpdateExporterRequest.newBuilder()
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Arguments validation error", status.description)
            assertThat(
                violations(exception),
                containsInAnyOrder(
                    Pair("name", "must not be blank"),
                    Pair("street", "must not be blank"),
                    Pair("paymentTerms", "must not be null"),
                    Pair("city", "must not be blank"),
                    Pair("country", "must not be blank"),
                    Pair("incoterm", "must not be null"),
                    Pair("zipCode", "must not be blank"),
                    Pair("currency", "must not be null"),
                    Pair("id", "must not be blank"),
                    Pair(
                        "id",
                        "must match \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$\""
                    )
                )
            )
        }
    }

    @Test
    fun `should delete a exporter successfully`() {
        val exporter = repository.save(createExporter())

        grpcClient.delete(
            DeleteExporterRequest.newBuilder()
                .setId(exporter.id.toString())
                .build()
        )
    }

    @Test
    fun `should not delete for not finding by id`() {
        val randomId = UUID.randomUUID().toString()
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteExporterRequest.newBuilder()
                    .setId(randomId)
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, exception.status.code)
        assertEquals("Exporter not found with id $randomId", exception.status.description)
    }

    @Test
    fun `should not delete an exporter due to invalid parameters`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteExporterRequest.newBuilder()
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Arguments validation error", status.description)
            assertThat(
                violations(this), containsInAnyOrder(
                    Pair("id", "must not be blank"),
                    Pair(
                        "id",
                        "must match \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$\""
                    )
                )
            )
        }
    }

    private fun createExporter(code: String? = null, name: String? = null) =
        Exporter(
            code = code ?: "12345678",
            name = name ?: "Test",
            paymentTerms = br.com.gn.exporter.PaymentTerms.E30,
            address = ExporterAddress("Test", "test", "test", "test"),
            incoterm = ExporterIncoterm.CIF,
            currency = ExporterCurrency.EUR,
            availabilityLT = 30,
            departureLT = 6,
            arrivalLT = 20,
            totalLT = 80
        )

}

@Factory
class Client {
    @Bean
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) =
        ExporterServiceGrpc.newBlockingStub(channel)
}
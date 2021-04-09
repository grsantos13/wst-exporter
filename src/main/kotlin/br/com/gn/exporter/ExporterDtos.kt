package br.com.gn.exporter

import br.com.gn.address.AddressRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

@Introspected
data class NewExporterRequest(
    @field:NotBlank @field:Size(max = 8) val code: String,
    @field:NotBlank val name: String,
    @field:NotNull val paymentTerms: PaymentTerms?,
    @field:NotNull @field:Valid val address: AddressRequest,
    @field:NotNull val incoterm: Incoterm?,
    @field:NotNull val currency: Currency?,
    @field:NotNull @field:PositiveOrZero val availabilityLT: Int? = null,
    @field:NotNull @field:PositiveOrZero val departureLT: Int? = null,
    @field:NotNull @field:PositiveOrZero val arrivalLT: Int? = null,
    @field:NotNull @field:PositiveOrZero val totalLT: Int? = null
) {
    fun toModel(): Exporter {
        return Exporter(
            code = code,
            name = name,
            paymentTerms = paymentTerms!!,
            address = address.toAddress(),
            incoterm = incoterm!!,
            currency = currency!!,
            availabilityLT = availabilityLT,
            departureLT = departureLT,
            arrivalLT = arrivalLT,
            totalLT = totalLT
        )
    }
}

@Introspected
data class UpdateExporterRequest(
    @field:NotBlank val name: String,
    @field:NotNull val paymentTerms: PaymentTerms?,
    @field:NotNull @field:Valid val address: AddressRequest,
    @field:NotNull val incoterm: Incoterm?,
    @field:NotNull val currency: Currency?,
    @field:NotNull @field:PositiveOrZero val availabilityLT: Int,
    @field:NotNull @field:PositiveOrZero val departureLT: Int,
    @field:NotNull @field:PositiveOrZero val arrivalLT: Int,
    @field:NotNull @field:PositiveOrZero val totalLT: Int
)

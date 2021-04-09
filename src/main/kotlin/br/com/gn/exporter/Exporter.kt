package br.com.gn.exporter

import br.com.gn.ExporterResponse
import br.com.gn.address.Address
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "exporter_code_uk", columnNames = ["code"])
    ]
)
class Exporter(
    @field:NotBlank @field:Size(max = 8) @Column(nullable = false, unique = true, updatable = false) val code: String,
    @field:NotBlank @Column(nullable = false) var name: String,
    @field:NotNull @Enumerated(STRING) @Column(nullable = false) var paymentTerms: PaymentTerms,
    @field:NotNull @Embedded var address: Address,
    @field:NotNull @Enumerated(STRING) @Column(nullable = false) var incoterm: Incoterm,
    @field:NotNull @Enumerated(STRING) @Column(nullable = false) var currency: Currency,
    @field:NotNull @field:PositiveOrZero val availabilityLT: Int?,
    @field:NotNull @field:PositiveOrZero val departureLT: Int?,
    @field:NotNull @field:PositiveOrZero val arrivalLT: Int?,
    @field:NotNull @field:PositiveOrZero val totalLT: Int?
) {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null

    fun update(request: UpdateExporterRequest) {
        this.address = request.address.toAddress()
        this.name = request.name
        this.paymentTerms = request.paymentTerms!!
        this.incoterm = request.incoterm!!
        this.currency = request.currency!!
    }

    fun toGrpcExporterResponse(): ExporterResponse {
        return ExporterResponse.newBuilder()
            .setId(id.toString())
            .setCode(code)
            .setName(name)
            .setPaymentTerms(br.com.gn.PaymentTerms.valueOf(paymentTerms.name))
            .setAddress(address.toGrpcAddress())
            .setCurrency(br.com.gn.Currency.valueOf(currency.name))
            .setIncoterm(br.com.gn.Incoterm.valueOf(incoterm.name))
            .setAvailabilityLT(availabilityLT ?: 0)
            .setDepartureLT(departureLT ?: 0)
            .setArrivalLT(arrivalLT ?: 0)
            .setTotalLT(totalLT ?: 0)
            .build()
    }
}

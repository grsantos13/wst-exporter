package br.com.gn.address

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class AddressRequest(
    @field:NotBlank val street: String,
    @field:NotBlank val city: String,
    @field:NotBlank val zipCode: String,
    @field:NotBlank val country: String
) {
    fun toAddress(): Address {
        return Address(
            street = street,
            city = city,
            zipCode = zipCode,
            country = country
        )
    }
}

class AddressResponse(address: Address) {
    val street = address.street
    val city = address.city
    val zipCode = address.zipCode
    val country = address.country
}
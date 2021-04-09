package br.com.gn.address

import br.com.gn.Address
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class Address(
    @field:NotBlank @Column(nullable = false) val street: String,
    @field:NotBlank @Column(nullable = false) val city: String,
    @field:NotBlank @Column(nullable = false) val zipCode: String,
    @field:NotBlank @Column(nullable = false) val country: String
) {
    fun toGrpcAddress(): Address {
        return Address.newBuilder()
            .setCity(this.city)
            .setCountry(this.country)
            .setStreet(this.street)
            .setZipCode(this.zipCode)
            .build()
    }
}
package br.com.gn.address

import br.com.gn.Address

fun Address.toRequestModel(): AddressRequest {
    return AddressRequest(
        street = street,
        city = city,
        zipCode = zipCode,
        country = country
    )
}
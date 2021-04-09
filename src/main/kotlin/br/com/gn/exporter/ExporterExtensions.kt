package br.com.gn.exporter

import br.com.gn.NewExporterRequest
import br.com.gn.UpdateExporterRequest
import br.com.gn.address.toRequestModel
import br.com.gn.utils.toEnum
import br.com.gn.exporter.NewExporterRequest as Request
import br.com.gn.exporter.UpdateExporterRequest as UpdateRequest

fun NewExporterRequest.toRequestModel(): Request {
    return Request(
        code = code,
        name = name,
        paymentTerms = paymentTerms.name.toEnum<PaymentTerms>(),
        address = address.toRequestModel(),
        incoterm = incoterm.name.toEnum<Incoterm>(),
        currency = currency.name.toEnum<Currency>(),
        availabilityLT = availabilityLT,
        departureLT = departureLT,
        arrivalLT = arrivalLT,
        totalLT = totalLT,
    )
}


fun UpdateExporterRequest.toRequestModel(): UpdateRequest {
    return UpdateRequest(
        name = name,
        paymentTerms = paymentTerms.name.toEnum<PaymentTerms>(),
        address = address.toRequestModel(),
        incoterm = incoterm.name.toEnum<Incoterm>(),
        currency = currency.name.toEnum<Currency>(),
        availabilityLT = availabilityLT,
        departureLT = departureLT,
        arrivalLT = arrivalLT,
        totalLT = totalLT,
    )
}
package br.com.gn.utils

import br.com.gn.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.Pageable as MicronautPageable


fun Pageable.toPageable(): MicronautPageable {
    return MicronautPageable.from(
        page,
        size,
        Sort.of(Sort.Order(orderBy, Sort.Order.Direction.valueOf(direction.name), true))
    )
}
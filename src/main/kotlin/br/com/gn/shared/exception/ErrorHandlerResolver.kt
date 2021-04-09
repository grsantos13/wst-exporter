package br.com.gn.shared.exception

import javax.inject.Singleton

@Singleton
class ErrorHandlerResolver(
    private val handlers: List<ExceptionHandler<Exception>>
) {

    fun resolve(e: Exception): ExceptionHandler<Exception> {
        val filteredHandlers = handlers.filter { it.supports(e) }
        return filteredHandlers.first()
    }

}
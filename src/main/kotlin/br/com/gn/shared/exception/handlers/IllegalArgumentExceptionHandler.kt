package br.com.gn.shared.exception.handlers

import br.com.gn.shared.exception.ExceptionHandler
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class IllegalArgumentExceptionHandler : ExceptionHandler<IllegalArgumentException> {
    override fun handle(e: IllegalArgumentException): ExceptionHandler.StatusWrapper {
        return ExceptionHandler.StatusWrapper(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is IllegalArgumentException
    }
}
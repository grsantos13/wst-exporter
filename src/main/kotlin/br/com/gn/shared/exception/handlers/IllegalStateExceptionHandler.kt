package br.com.gn.shared.exception.handlers

import br.com.gn.shared.exception.ExceptionHandler
import br.com.gn.shared.exception.ExceptionHandler.StatusWrapper
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class IllegalStateExceptionHandler : ExceptionHandler<IllegalStateException> {
    override fun handle(e: IllegalStateException): StatusWrapper {
        return StatusWrapper(
            Status.FAILED_PRECONDITION
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is IllegalStateException
    }
}
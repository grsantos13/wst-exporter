package br.com.gn.shared.exception.handlers

import br.com.gn.shared.exception.ExceptionHandler
import br.com.gn.shared.exception.ObjectNotFoundException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ObjectNotFoundExceptionHandler : ExceptionHandler<ObjectNotFoundException> {
    override fun handle(e: ObjectNotFoundException): ExceptionHandler.StatusWrapper {
        return ExceptionHandler.StatusWrapper(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ObjectNotFoundException
    }
}
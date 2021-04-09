package br.com.gn.shared.exception

import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

interface ExceptionHandler<T : Exception> {


    fun handle(e: T): StatusWrapper

    fun supports(e: Exception): Boolean


    data class StatusWrapper(val status: Status, val metadata: Metadata = Metadata()) {
        constructor(statusException: StatusRuntimeException) : this(
            statusException.status,
            statusException.trailers ?: Metadata()
        )

        constructor(statusProto: com.google.rpc.Status) : this(StatusProto.toStatusRuntimeException(statusProto))

        fun asRuntimeException(): StatusRuntimeException {
            return status.asRuntimeException(metadata)
        }
    }

}
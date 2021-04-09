package br.com.gn.util

import com.google.rpc.BadRequest
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

class StatusRuntimeExceptionUtils {

    companion object {

        fun violations(exception: StatusRuntimeException): List<Pair<String, String>> {
            return StatusProto.fromThrowable(exception)
                ?.detailsList?.get(0)!!.unpack(BadRequest::class.java)
                .fieldViolationsList
                .map { Pair(it.field, it.description) }
        }

    }

}
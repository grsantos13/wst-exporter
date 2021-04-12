package br.com.gn.exporter.register

import br.com.gn.ExporterAwaitingRegistrationRequest
import br.com.gn.ExporterAwaitingRegistrationServiceGrpc
import br.com.gn.ExportersAwaitingRegistrationResponse
import br.com.gn.ExportersAwaitingRegistrationResponse.ExporterAwaitingRegistrationResponse
import br.com.gn.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional

@ErrorHandler
@Singleton
class AwaitingRegistrationEndpoint(
    private val manager: EntityManager
) :
    ExporterAwaitingRegistrationServiceGrpc.ExporterAwaitingRegistrationServiceImplBase() {

    @Transactional
    override fun read(
        request: ExporterAwaitingRegistrationRequest,
        responseObserver: StreamObserver<ExportersAwaitingRegistrationResponse>
    ) {
        val list = manager.createQuery(" select r from Register r ", Register::class.java)
            .resultList
            .map {
                ExporterAwaitingRegistrationResponse.newBuilder()
                    .setCode(it.code)
                    .setName(it.name)
                    .build()
            }

        responseObserver.onNext(
            ExportersAwaitingRegistrationResponse.newBuilder()
                .addAllExporters(list)
                .build()
        )
        responseObserver.onCompleted()
    }
}
package br.com.gn.exporter

import br.com.gn.*
import br.com.gn.NewExporterRequest
import br.com.gn.UpdateExporterRequest
import br.com.gn.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.transaction.Transactional

@ErrorHandler
@Singleton
class ExporterEndpoint(
    private val service: ExporterService
) : ExporterServiceGrpc.ExporterServiceImplBase() {

    @Transactional
    override fun create(request: NewExporterRequest, responseObserver: StreamObserver<ExporterResponse>) {
        val exporter = service.create(request = request.toRequestModel())
        responseObserver.onNext(exporter.toGrpcExporterResponse())
        responseObserver.onCompleted()
    }


    @Transactional
    override fun read(request: ReadExporterRequest, responseObserver: StreamObserver<ExportersResponse>) {
        val responseList = service.read(request.name)
            .map { it.toGrpcExporterResponse() }

        val response = ExportersResponse.newBuilder()
            .addAllExporters(responseList)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    @Transactional
    override fun update(request: UpdateExporterRequest, responseObserver: StreamObserver<ExporterResponse>) {
        val exporter = service.update(request = request.toRequestModel(), id = request.id)
        responseObserver.onNext(exporter.toGrpcExporterResponse())
        responseObserver.onCompleted()
    }

    @Transactional
    override fun delete(request: DeleteExporterRequest, responseObserver: StreamObserver<ExporterResponse>) {
        val exporter = service.delete(request.id)
        responseObserver.onNext(exporter.toGrpcExporterResponse())
        responseObserver.onCompleted()
    }
}

package br.com.gn.shared.exception

import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton

@Singleton
class ErrorInterceptor(
    private val resolver: ErrorHandlerResolver
) : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        return try {
            context.proceed()
        } catch (e: Exception) {
            val handler = resolver.resolve(e)
            val statusWrapper = handler.handle(e)
            val observer = context.parameterValues[1] as StreamObserver<*>
            observer.onError(statusWrapper.asRuntimeException())
            null
        }
    }
}
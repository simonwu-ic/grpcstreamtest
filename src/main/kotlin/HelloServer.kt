import com.example.hello.HelloResponse
import com.example.hello.HelloServiceGrpcKt
import com.google.protobuf.Empty
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HelloServer {

    private val port = 50051
    private val server: Server = ServerBuilder.forPort(port)
        .addService(HelloServiceImpl())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloServiceImpl : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
        override fun streamHello(request: Empty): Flow<HelloResponse> = flow {
            println("Client connected for streaming")

            // Start streaming hello world messages continuously
            var counter = 1
            while (true) {
                val response = HelloResponse.newBuilder()
                    .setMessage("Hello World #$counter from gRPC Server!")
                    .build()

                emit(response)
                println("Sent: Hello World #$counter")

                counter++
                delay(1000) // Send a message every second
            }
        }
    }
}

fun main() {
    val server = HelloServer()
    server.start()
    server.blockUntilShutdown()
}

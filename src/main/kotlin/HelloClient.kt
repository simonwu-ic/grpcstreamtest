import com.example.hello.HelloServiceGrpcKt
import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class HelloClient {
    private lateinit var channel: ManagedChannel

    private lateinit var stub: HelloServiceGrpcKt.HelloServiceCoroutineStub

    suspend fun startStreaming() {
        val request = Empty.getDefaultInstance()
        val channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build()
        val stub = HelloServiceGrpcKt.HelloServiceCoroutineStub(channel)
        val stream = stub.streamHello(request)

        while (true) {
            coroutineScope {
                val dispatchers = List(10) { index ->
                    launch(Dispatchers.IO) {
                        try {
                            // Create a new channel and stub for each parallel instance

                            stream
                                .retry(3) { ex ->
                                    println("Retrying due to $ex, thread_id: ${Thread.currentThread().id}")
                                    delay(1000)
                                    true
                                }
                                .catch { ex ->
                                    println("Catch due to $ex, thread_id: ${Thread.currentThread().id}")
                                }
                                .collect { response ->
                                    println("Received: ${response.message}, thread_id: ${Thread.currentThread().id}")
                                }
                        } catch (e: Exception) {
                            println("Error: ${e.message}")
                        }
                    }
                }

                // Wait for all parallel coroutines to complete
                dispatchers.joinAll()
            }
        }
    }

    fun shutdown() {
        channel.shutdown()
    }
}

fun main() = runBlocking {
    val client = HelloClient()

    try {
        println("Starting client...")
        client.startStreaming()
    } finally {
        client.shutdown()
    }
}

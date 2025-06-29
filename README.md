# gRPC Stream Test

A Kotlin gRPC server that implements bidirectional streaming to continuously send "Hello World" messages.

## Features

- Bidirectional streaming gRPC service
- Continuous "Hello World" message streaming
- Kotlin coroutines-based implementation
- Auto-generated gRPC stubs from proto files

## How to Run

### 1. Generate Proto Files and Build

```bash
./gradlew build
```

### 2. Start the Server

```bash
./gradlew run
```

The server will start on port 50051 and begin streaming "Hello World" messages.

### 3. Test with Client (in another terminal)

```bash
./gradlew runClient
```

## Architecture

- `hello.proto` - Protocol Buffer service definition
- `HelloServer.kt` - gRPC server implementation with streaming
- `HelloClient.kt` - Simple client to test the streaming server
- Bidirectional streaming: client sends requests, server responds with continuous stream

The server continuously sends numbered "Hello World" messages every second once a client connects.
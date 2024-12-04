package co.karim;

import com.google.flatbuffers.FlatBufferBuilder;
import flatbuffers.ledger.TransactionRequest;
import flatbuffers.ledger.TransactionResponse;
import flatbuffers.ledger.TransactionServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionsClient {

    private static final int THREADS = 50; // Number of concurrent threads (virtual users)
    private static final int DURATION_SECONDS = 60; // Duration of the load test in seconds
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
        // Create gRPC channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();
        var client = TransactionServiceGrpc.newBlockingStub(channel);

        // Prepare the request payload
        byte[] requestBytes = createTransactionRequest();

        // Metrics
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger totalRequests = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        // Start time
        long startTime = System.currentTimeMillis();
        long endTime = startTime + DURATION_SECONDS * 1000L;

        // Submit tasks for the duration
        CountDownLatch latch = new CountDownLatch(THREADS);
        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                while (System.currentTimeMillis() < endTime) {
                    long start = System.nanoTime();
                    try {
                        // Make gRPC call
                        TransactionResponse response = client.processTransaction(
                                TransactionRequest.getRootAsTransactionRequest(ByteBuffer.wrap(requestBytes))
                        );
                        long end = System.nanoTime();
                        latencies.add(end - start);
                        totalRequests.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Request failed: " + e.getMessage());
                    }
                }
                latch.countDown();
            });
        }

        // Wait for all threads to complete
        latch.await();
        executor.shutdown();

        // Metrics Calculation
        double totalTimeSeconds = DURATION_SECONDS;
        double tps = totalRequests.get() / totalTimeSeconds;
        List<Long> sortedLatencies = new ArrayList<>(latencies);
        Collections.sort(sortedLatencies);

        double averageLatencyMs = sortedLatencies.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
        double p90LatencyMs = getPercentile(sortedLatencies, 90) / 1_000_000.0;
        double p95LatencyMs = getPercentile(sortedLatencies, 95) / 1_000_000.0;
        double p99LatencyMs = getPercentile(sortedLatencies, 99) / 1_000_000.0;

        // Print Metrics
        System.out.println("Summary:");
        System.out.println("Total Requests: " + totalRequests.get());
        System.out.println("Concurrent Users: " + THREADS);
        System.out.println("Duration: " + totalTimeSeconds + " seconds");
        System.out.println("Transactions Per Second (TPS): " + tps);
        System.out.println("Average Latency: " + averageLatencyMs + " ms");
        System.out.println("P90 Latency: " + p90LatencyMs + " ms");
        System.out.println("P95 Latency: " + p95LatencyMs + " ms");
        System.out.println("P99 Latency: " + p99LatencyMs + " ms");

        // Clean up
        channel.shutdown();
    }

    private static byte[] createTransactionRequest() {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        TransactionRequest.startTransactionRequest(builder);
        TransactionRequest.addTransactionId(builder, 1);
        TransactionRequest.addDebitAccount(builder, 100);
        TransactionRequest.addCreditAccount(builder, 200);
        TransactionRequest.addAmount(builder, 1000);
        TransactionRequest.addCurrency(builder, 1);
        int requestOffset = TransactionRequest.endTransactionRequest(builder);
        builder.finish(requestOffset);
        return builder.sizedByteArray();
    }

    private static long getPercentile(List<Long> latencies, int percentile) {
        if (latencies.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil(percentile / 100.0 * latencies.size()) - 1;
        return latencies.get(index);
    }
}

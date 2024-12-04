package co.karim;

import com.lmax.disruptor.dsl.Disruptor;
import flatbuffers.ledger.TransactionRequest;
import flatbuffers.ledger.TransactionResponse;
import flatbuffers.ledger.TransactionServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TransactionService extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final Disruptor<TransactionEvent> disruptor;

    public TransactionService(Disruptor<TransactionEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void processTransaction(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        long sequence = disruptor.getRingBuffer().next();
        try {
            TransactionEvent event = disruptor.getRingBuffer().get(sequence);
            event.set(request, responseObserver);
        } finally {
            disruptor.getRingBuffer().publish(sequence);
        }
    }
}

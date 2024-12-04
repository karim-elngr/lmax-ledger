package co.karim;

import flatbuffers.ledger.TransactionRequest;
import flatbuffers.ledger.TransactionResponse;
import io.grpc.stub.StreamObserver;

public class TransactionEvent {

    private TransactionRequest request;
    private StreamObserver<TransactionResponse> responseObserver;
    private long timestamp;
    private boolean status;

    public void set(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        this.request = request;
        this.responseObserver = responseObserver;
        this.timestamp = 0;
        this.status = false;
    }

    public TransactionRequest getRequest() {
        return request;
    }

    public StreamObserver<TransactionResponse> getResponseObserver() {
        return responseObserver;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}

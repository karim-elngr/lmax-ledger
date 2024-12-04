package co.karim;

import com.google.flatbuffers.FlatBufferBuilder;
import com.lmax.disruptor.EventHandler;
import flatbuffers.ledger.TransactionResponse;

import java.nio.ByteBuffer;

public class TransactionResponseHandler implements EventHandler<TransactionEvent> {

    @Override
    public void onEvent(TransactionEvent event, long sequence, boolean endOfBatch) {

        var request = event.getRequest();
        var responseObserver = event.getResponseObserver();

        var builder = new FlatBufferBuilder(1024);
        TransactionResponse.startTransactionResponse(builder);
        TransactionResponse.addTransactionId(builder, request.transactionId());
        TransactionResponse.addSuccess(builder, event.getStatus());
        TransactionResponse.addTimestamp(builder, event.getTimestamp());
        int responseOffset = TransactionResponse.endTransactionResponse(builder);

        builder.finish(responseOffset);
        byte[] responseBytes = builder.sizedByteArray();

        responseObserver.onNext(TransactionResponse.getRootAsTransactionResponse(ByteBuffer.wrap(responseBytes)));
        responseObserver.onCompleted();
    }
}

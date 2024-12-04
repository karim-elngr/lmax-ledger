package co.karim;

import com.lmax.disruptor.dsl.Disruptor;
import io.grpc.ServerBuilder;
import org.rocksdb.RocksDBException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class TransactionServer {

    public static void main(String[] args) throws InterruptedException, IOException, RocksDBException {

        var disruptor = setupDisruptor();

        var server = ServerBuilder
                .forPort(8080)
                .addService(new TransactionService(disruptor))
                .build();

        server.start();
        server.awaitTermination();
    }

    private static Disruptor<TransactionEvent> setupDisruptor() throws RocksDBException {

        var bufferSize = 1024;
        var disruptor = new Disruptor<>(
                TransactionEvent::new,
                bufferSize,
                Executors.defaultThreadFactory()
        );

        disruptor.handleEventsWith(new TransactionProcessor(setupDatabase()))
                .then(new TransactionResponseHandler());
        disruptor.start();

        return disruptor;
    }

    public static Database setupDatabase() throws RocksDBException {
        var database = new Database();
        database.initialize();
        database.executeTransaction((db, write, read) -> {
            var creditAccountAddr = longToBytes(200);
            var debitAccountAddr = longToBytes(100);
            var creditAccountBalance = bytesToLong(db.get(read, creditAccountAddr));
            var debitAccountBalance = bytesToLong(db.get(read, debitAccountAddr));
            System.out.printf("Credit account balance: %d%n", creditAccountBalance);
            System.out.printf("Debit account balance: %d%n", debitAccountBalance);
        });
        return database;
    }

    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }
}

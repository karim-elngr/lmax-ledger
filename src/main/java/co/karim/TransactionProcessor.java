package co.karim;

import com.lmax.disruptor.EventHandler;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


public class TransactionProcessor implements EventHandler<TransactionEvent> {

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessor.class);

    private final Database database;

    public TransactionProcessor(Database database) {
        this.database = database;
    }

    @Override
    public void onEvent(TransactionEvent event, long sequence, boolean endOfBatch) {

        var request = event.getRequest();
        event.setTimestamp(System.currentTimeMillis());

        try {
            database.executeTransaction((txn, writeOptions, readOptions) -> {
                try {
                    var creditAccountAddr = longToBytes(event.getRequest().creditAccount());
                    var debitAccountAddr = longToBytes(event.getRequest().debitAccount());
                    var creditAccount = txn.get(readOptions, creditAccountAddr);
                    var debitAccount = txn.get(readOptions, debitAccountAddr);
                    var creditAccountBalance = creditAccount != null? bytesToLong(creditAccount) : 0;
                    var debitAccountBalance = debitAccount != null? bytesToLong(debitAccount) : 0;
                    txn.put(writeOptions, creditAccountAddr, longToBytes(creditAccountBalance + event.getRequest().amount()));
                    txn.put(writeOptions, debitAccountAddr, longToBytes(debitAccountBalance - event.getRequest().amount()));
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
            });
            event.setTimestamp(System.currentTimeMillis());
            event.setStatus(true);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", request.transactionId(), e);
        }
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

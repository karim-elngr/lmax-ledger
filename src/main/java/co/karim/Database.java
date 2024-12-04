package co.karim;

import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.TransactionDB;
import org.rocksdb.TransactionDBOptions;
import org.rocksdb.WriteOptions;

import java.util.function.BiConsumer;

public class Database implements AutoCloseable {

    static {
        RocksDB.loadLibrary();
    }

    private final String dbPath = "./rocksdb-data";

    private Options options;
    private TransactionDBOptions txnDbOptions;
    private RocksDB txnDb;
    private WriteOptions writeOptions;
    private ReadOptions readOptions;

    public void initialize() throws RocksDBException {
        options = new Options().setCreateIfMissing(true);
        txnDbOptions = new TransactionDBOptions();
        txnDb = TransactionDB.open(options, txnDbOptions, dbPath);
        writeOptions = new WriteOptions();
        readOptions = new ReadOptions();
    }

    public void close() {
        readOptions.close();
        writeOptions.close();
        txnDb.close();
        txnDbOptions.close();
        options.close();
    }

    public void executeTransaction(TriConsumer<RocksDB, WriteOptions, ReadOptions> transactionOperations) throws RocksDBException {

        transactionOperations.accept(txnDb, writeOptions, readOptions);
    }

    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v) throws RocksDBException;
    }
}

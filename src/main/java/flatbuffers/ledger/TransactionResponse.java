// automatically generated by the FlatBuffers compiler, do not modify

package flatbuffers.ledger;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.BooleanVector;
import com.google.flatbuffers.ByteVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.DoubleVector;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.FloatVector;
import com.google.flatbuffers.IntVector;
import com.google.flatbuffers.LongVector;
import com.google.flatbuffers.ShortVector;
import com.google.flatbuffers.StringVector;
import com.google.flatbuffers.Struct;
import com.google.flatbuffers.Table;
import com.google.flatbuffers.UnionVector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class TransactionResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_24_3_25(); }
  public static TransactionResponse getRootAsTransactionResponse(ByteBuffer _bb) { return getRootAsTransactionResponse(_bb, new TransactionResponse()); }
  public static TransactionResponse getRootAsTransactionResponse(ByteBuffer _bb, TransactionResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public TransactionResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public long transactionId() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long debitAccount() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long creditAccount() { int o = __offset(8); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long amount() { int o = __offset(10); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long currency() { int o = __offset(12); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public boolean success() { int o = __offset(14); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }
  public long timestamp() { int o = __offset(16); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }

  public static int createTransactionResponse(FlatBufferBuilder builder,
      long transactionId,
      long debitAccount,
      long creditAccount,
      long amount,
      long currency,
      boolean success,
      long timestamp) {
    builder.startTable(7);
    TransactionResponse.addTimestamp(builder, timestamp);
    TransactionResponse.addCurrency(builder, currency);
    TransactionResponse.addAmount(builder, amount);
    TransactionResponse.addCreditAccount(builder, creditAccount);
    TransactionResponse.addDebitAccount(builder, debitAccount);
    TransactionResponse.addTransactionId(builder, transactionId);
    TransactionResponse.addSuccess(builder, success);
    return TransactionResponse.endTransactionResponse(builder);
  }

  public static void startTransactionResponse(FlatBufferBuilder builder) { builder.startTable(7); }
  public static void addTransactionId(FlatBufferBuilder builder, long transactionId) { builder.addLong(0, transactionId, 0L); }
  public static void addDebitAccount(FlatBufferBuilder builder, long debitAccount) { builder.addLong(1, debitAccount, 0L); }
  public static void addCreditAccount(FlatBufferBuilder builder, long creditAccount) { builder.addLong(2, creditAccount, 0L); }
  public static void addAmount(FlatBufferBuilder builder, long amount) { builder.addLong(3, amount, 0L); }
  public static void addCurrency(FlatBufferBuilder builder, long currency) { builder.addLong(4, currency, 0L); }
  public static void addSuccess(FlatBufferBuilder builder, boolean success) { builder.addBoolean(5, success, false); }
  public static void addTimestamp(FlatBufferBuilder builder, long timestamp) { builder.addLong(6, timestamp, 0L); }
  public static int endTransactionResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }
  public static void finishTransactionResponseBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedTransactionResponseBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public TransactionResponse get(int j) { return get(new TransactionResponse(), j); }
    public TransactionResponse get(TransactionResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}


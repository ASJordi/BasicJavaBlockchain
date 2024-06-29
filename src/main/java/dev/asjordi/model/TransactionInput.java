package dev.asjordi.model;

/**
 * The TransactionInput class represents an input to a transaction in a blockchain network.
 * Each TransactionInput references an unspent transaction output (UTXO) from a previous transaction.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class TransactionInput {

    /**
     * The ID of the UTXO transaction output references.
     */
    private String transactionOutputId;

    /**
     * The UTXO transaction output references.
     */
    private TransactionOutput UTXO;

    /**
     * Constructor for the TransactionInput class.
     * @param transactionOutputId The ID of the UTXO transaction output references.
     */
    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    /**
     * @return The ID of the UTXO transaction output references.
     */
    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    /**
     * Sets the ID of the UTXO transaction output references.
     * @param transactionOutputId The ID of the UTXO transaction output references.
     */
    public void setTransactionOutputId(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    /**
     * @return The UTXO transaction output references.
     */
    public TransactionOutput getUTXO() {
        return UTXO;
    }

    /**
     * Sets the UTXO transaction output references.
     * @param UTXO The UTXO transaction output references.
     */
    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }
}

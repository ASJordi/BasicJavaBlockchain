package dev.asjordi.model;

import dev.asjordi.util.StringUtil;
import java.security.PublicKey;

/**
 * The TransactionOutput class represents an output of a transaction in a blockchain network.
 * Each TransactionOutput has an ID, a recipient, a value and a parent transaction ID.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class TransactionOutput {

    private final String id;
    private PublicKey recipient;
    private float value;
    private String parentTransactionId;

    /**
     * Constructor for the TransactionOutput class.
     * @param recipient The new owner of these coins.
     * @param value The amount of coins they own.
     * @param parentTransactionId The ID of the parent transaction.
     */
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
            StringUtil.getStringFromKey(this.recipient) +
            Float.toString(this.value) +
            this.parentTransactionId
        );
    }
    
    /**
     * Check if a coin belongs to a specific user.
     * @param publicKey The public key of the user to check.
     * @return True if the coin belongs to the user, false otherwise.
     */
    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(this.recipient);
    }

    /**
     * @return The ID of the TransactionOutput.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The recipient's public key.
     */
    public PublicKey getRecipient() {
        return recipient;
    }

    /**
     * @return The value of the TransactionOutput.
     */
    public float getValue() {
        return value;
    }

}

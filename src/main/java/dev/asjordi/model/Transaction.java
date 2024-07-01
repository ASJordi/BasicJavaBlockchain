package dev.asjordi.model;

import dev.asjordi.Main;
import dev.asjordi.util.StringUtil;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Transaction class represents a transaction in a blockchain network.
 * Each Transaction has a trnasaction ID, sender's public key, recipient's public key, value, signature, inputs and outputs.
 * It also maintains a sequence number to keep track of the number of transactions generated.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Transaction {

    public String transactionId;
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;
    
    public List<TransactionInput> inputs;
    public List<TransactionOutput> outputs;

    /**
     * A rough count of how many transactions have been generated
     */
    private static int sequence = 0;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Constructor for the Transaction class.
     * @param from The sender's public key.
     * @param to The recipient's public key.
     * @param value The amount to be sent.
     * @param inputs The inputs for the transaction.
     */
    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        this.outputs = new LinkedList<>();
    }
    
    /**
     * Signs all the data we don;t wish to be tampered with.
     * @param privateKey The private key to sign the data with.
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.recipient) + Float.toString(this.value);
        this.signature = StringUtil.applyECDSASignature(privateKey, data);
    }
    
    /**
     * Verifies the data we signed hasn't been tampered with.
     * @return True if the signature is valid, false otherwise.
     */
    public boolean verifySignature() {
	String data = StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.recipient) + Float.toString(this.value)	;
	return StringUtil.verifyECDSASignature(this.sender, data, this.signature);
    }
    
    /**
     * Processes the transaction and generates the outputs.
     * @return True if new transaction could be created, false otherwise.
     */
    public boolean processTransaction() {
        
        if (verifySignature() == false) {
            LOGGER.log(Level.WARNING, "Transaction Signature failed to verify");
            return false;
        }
        
        // Gather transaction inputs (Make sure they are unspent)
        for (TransactionInput i : this.inputs) {
            i.setUTXO(Main.UTXOs.get(i.getTransactionOutputId()));
        }
        
        // Check if Transaction is valid
        if (getInputsValue() < Main.minimumTransaction) {
            LOGGER.log(Level.WARNING, "Transaction Inputs too small: {0}", getInputsValue());
            return false;
        }
        
        // Generate Transaction outputs
        float leftOver = getInputsValue() - this.value; // Get value of inputs then the left over change
        this.transactionId = this.calculateHash();
        this.outputs.add(new TransactionOutput(this.recipient, this.value, this.transactionId)); // Send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, this.transactionId));
        
        // Add outputs to unspent list
        for(TransactionOutput o : this.outputs) {
            Main.UTXOs.put(o.getId(), o);
        }
        
        // Remove transaction inputs from UTXO lists as spent
        for(TransactionInput i : this.inputs) {
            if (i.getUTXO() == null) continue; // If transaction can't be found skip it
            Main.UTXOs.remove(i.getUTXO().getId());
        }
        
        return true;
    }
    
    /**
     * Returns the sum of inputs (UTXOs) values
     * @return The total value of the inputs.
     */
    public float getInputsValue() {
        float total = 0;
        
        for (TransactionInput i : this.inputs) {
            if (i.getUTXO() == null) continue;
            total += i.getUTXO().getValue();
        }
        
        return total;
    }
    
    /**
     * @return The total value of the outputs.
     */
    public float getOutputsValue() {
        float total = 0;
        
        for (TransactionOutput o : this.outputs) {
            total += o.getValue();
        }
        
        return total;
    }
    
    /**
     * Calculates the transaction hash, which will be used as the transaction ID.
     * @return  The transaction hash.
     */
    private String calculateHash() {
        sequence++;
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(this.sender) +
            StringUtil.getStringFromKey(this.recipient) +
            Float.toString(this.value) +
            sequence
        );
    }
    
}

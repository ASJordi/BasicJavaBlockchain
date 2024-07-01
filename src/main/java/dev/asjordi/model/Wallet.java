package dev.asjordi.model;

import dev.asjordi.Main;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Wallet class represents a wallet in a blockchain network.
 * Each wallet has a private key and a public key that are used to sign transactions.
 * It also keeps a record of the unspent transaction outputs (UTXOs) it owns.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Wallet {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Map<String, TransactionOutput> UTXOs;
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Wallet class constructor.
     * Initializes the UTXOs map and generates a new key pair.
     */
    public Wallet() {
        this.UTXOs = new HashMap<>();
        this.generateKeyPair();
    }

    /**
     * Generates a public and private key pair for this wallet using ECDSA algorithm.
     * @throws RuntimeException if the key pair cannot be generated.
     */
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecgps = new ECGenParameterSpec("prime192v1");
            keyGenerator.initialize(ecgps, random);
            KeyPair keyPair = keyGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Calculates the balance of this wallet by summing the value of all its UTXOs.
     * @return The total balance of this wallet.
     */
    public float getBalance() {
        float total = 0;
        
        for (Map.Entry<String, TransactionOutput> item : Main.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            
            if (UTXO.isMine(this.publicKey)) {
                this.UTXOs.put(UTXO.getId(), UTXO);
                total += UTXO.getValue();
            }
        }
        
        return total;
    }
    
    /**
     * Creates a new transaction from this wallet to the recipient's public key.
     * @param _recipient The public key of the transaction recipient.
     * @param value The value (amount) of the transaction.
     * @return The new transaction if there are sufficient funds, null otherwise.
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        
        if (getBalance() < value) {
            LOGGER.log(Level.WARNING, "Not enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        
        List<TransactionInput> inputs = new LinkedList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : this.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getId()));
            if (total > value) break;
        }
        
        Transaction newTransaction = new Transaction(this.publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(this.privateKey);
        
        for(TransactionInput input : inputs) {
            this.UTXOs.remove(input.getTransactionOutputId());
        }
        
        return newTransaction;
    }

    /**
     * @return This wallet's public key.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * @return This wallet's private key.
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * @return This wallet's UTXOs map.
     */
    public Map<String, TransactionOutput> getUTXOs() {
        return UTXOs;
    }

    /**
     * @return A string representation of this wallet, which includes its public key.
     */
    @Override
    public String toString() {
        return "Wallet{" + "publicKey=" + publicKey + '}';
    }

    /**
     * @return A hash code value for this wallet.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.privateKey);
        hash = 59 * hash + Objects.hashCode(this.publicKey);
        hash = 59 * hash + Objects.hashCode(this.UTXOs);
        return hash;
    }

    /**
     * Compares this Wallet object with another object for equality.
     * @param obj The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Wallet other = (Wallet) obj;
        if (!Objects.equals(this.privateKey, other.privateKey)) return false;
        if (!Objects.equals(this.publicKey, other.publicKey)) return false;
        return Objects.equals(this.UTXOs, other.UTXOs);
    }
}

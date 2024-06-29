package dev.asjordi.model;

import dev.asjordi.util.StringUtil;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The Block class represents a block in a blockchain network.
 * Each Block has a hash, previous hash, merkle root, list of transactions, timestamp, and nonce value.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Block {
    
    private String hash;
    private String previousHash;
    private String merkleRoot;
    private List<Transaction> transactions;
    private Long timeStamp;
    private Integer nonce;

    /**
     * Constructor for the Block class.
     * @param previousHash The hash of the previous block in the blockchain.
     */
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.nonce = 0;
        this.transactions = new LinkedList<>();
        this.hash = this.calculateHash();
    }
    
    /**
     * Calculates the hash of the block based on its contents.
     * @return The hash of the block.
     */
    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
            this.previousHash +
            Long.toString(this.timeStamp) +
            Integer.toString(this.nonce) +
            this.merkleRoot
        );
        
        return calculatedHash;
    }
    
    /**
     * Increases nonce value until hash target is reached.
     * @param difficulty The difficulty of the proof of work.
     */
    public void mineBlock(int difficulty) {
        this.merkleRoot = StringUtil.getMerkleRoot(this.transactions);
        String target = StringUtil.getDifficultyString(difficulty);
        while (!this.hash.subSequence(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = this.calculateHash();
        }
        System.out.println("Block mined! -> " + this.hash);
    }
    
    /**
     * Adds transactions to this block.
     * Processes the transaction and checks if it's valid, unless the block is the genesis block.
     * @param t The Transaction to be added to the block.
     * @return True if the transaction has been successfully added to the block; false otherwise.
     */
    public boolean addTransaction(Transaction t) throws IllegalStateException {
        if (t == null) return false;
        if (!"0".equals(this.previousHash)) {
            if (t.processTransaction() != true) {
                System.out.println("#Transaction failed to process. Discarded.");
                return false;
            }
        }
        this.transactions.add(t);
        System.out.println("#Transaction successfully added to Block");
        return true;
    }

    /**
     * @return The hash of the block.
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return The hash of the previous block in the blockchain.
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * @return List of transactions in the block.
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return A string representation of the block.
     */
    @Override
    public String toString() {
        return "Block{" + "hash=" + hash + ", previousHash=" + previousHash +
                ", merkleRoot=" + merkleRoot + ", timeStamp=" + timeStamp +
                ", nonce=" + nonce + '}';
    }

    /**
     * @return Returns a hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.hash);
        hash = 79 * hash + Objects.hashCode(this.previousHash);
        hash = 79 * hash + Objects.hashCode(this.merkleRoot);
        hash = 79 * hash + Objects.hashCode(this.transactions);
        hash = 79 * hash + Objects.hashCode(this.timeStamp);
        hash = 79 * hash + Objects.hashCode(this.nonce);
        return hash;
    }

    /**
     * Indicates whether some other object is "equal to" this block.
     * @param obj The reference object with which to compare.
     * @return True if this block is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Block other = (Block) obj;
        if (!Objects.equals(this.hash, other.hash)) return false;
        if (!Objects.equals(this.previousHash, other.previousHash)) return false;
        if (!Objects.equals(this.merkleRoot, other.merkleRoot)) return false;
        if (!Objects.equals(this.transactions, other.transactions)) return false;
        if (!Objects.equals(this.timeStamp, other.timeStamp)) return false;
        return Objects.equals(this.nonce, other.nonce);
    }
}

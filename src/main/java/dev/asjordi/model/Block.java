package dev.asjordi.model;

import dev.asjordi.util.StringUtil;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Block {
    
    public String hash;
    public String previousHash;
    private String data;
    private Long timeStamp;
    private Integer nonce;
    
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.nonce = 0;
        this.hash = this.calculateHash();
    }
    
    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
            this.previousHash +
            Long.toString(this.timeStamp) +
            Integer.toString(this.nonce) +
            this.data
        );
        
        return calculatedHash;
    }
    
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!this.hash.subSequence(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = this.calculateHash();
        }
        System.out.println("Block mined! -> " + this.hash);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "Block{" + "hash=" + hash + ", previousHash=" + previousHash + ", data=" + data + ", timeStamp=" + timeStamp + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.hash);
        hash = 17 * hash + Objects.hashCode(this.previousHash);
        hash = 17 * hash + Objects.hashCode(this.data);
        hash = 17 * hash + Objects.hashCode(this.timeStamp);
        hash = 17 * hash + Objects.hashCode(this.nonce);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Block other = (Block) obj;
        if (!Objects.equals(this.hash, other.hash)) return false;
        if (!Objects.equals(this.previousHash, other.previousHash)) return false;
        if (!Objects.equals(this.data, other.data)) return false;
        if (!Objects.equals(this.timeStamp, other.timeStamp)) return false;
        return Objects.equals(this.nonce, other.nonce);
    }
    
    
    
}

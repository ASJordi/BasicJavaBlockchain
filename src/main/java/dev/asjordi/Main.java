
package dev.asjordi;

import com.google.gson.GsonBuilder;
import dev.asjordi.model.Block;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Main {
    
    public static List<Block> blockchain = new LinkedList<>();
    public static Integer difficulty = 5;
    
    public static void main(String[] args) {
        
        // Add our block to the blockchain
        blockchain.add(new Block("Hi, I am the first block", "0"));
        System.out.println("Trying to Mine Block 1...");
        blockchain.get(0).mineBlock(difficulty);
        
        blockchain.add(new Block("I am the second block",blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Trying to Mine Block 2...");
        blockchain.get(1).mineBlock(difficulty);
        
        
        blockchain.add(new Block("Hey, I am the third block",blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Trying to Mine Block 3...");
        blockchain.get(2).mineBlock(difficulty);
        
        System.out.println("\nBlockchain is valid: " + isChainValid());
        
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe blockchain: ");
        System.out.println(blockchainJson);
        
    }
    
    public static Boolean isChainValid() {
        Block currentBlock = null;
        Block previousBlock = null;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            // Compare registered hash and calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }
            // Compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.previousHash)) {
                return false;
            }
            // Check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                return false;
            }
        }
        return true;
    }
}

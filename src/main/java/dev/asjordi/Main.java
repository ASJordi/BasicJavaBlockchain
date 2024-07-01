package dev.asjordi;

import dev.asjordi.logger.MyLogger;
import dev.asjordi.model.Block;
import dev.asjordi.model.Transaction;
import dev.asjordi.model.TransactionInput;
import dev.asjordi.model.TransactionOutput;
import dev.asjordi.model.Wallet;

import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Main class to test the blockchain implementation
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static List<Block> blockchain = new LinkedList<>();
    // Contains all unspent transactions
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

    public static Integer difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        LOGGER.setLevel(Level.ALL);

        try {
            MyLogger.setup();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        // Create wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();
        
        // Create genesis transaction, which sends 100 coins to walletA
        genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        // Manually signs the genesis transaction
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        // Manually set the transaction id
        genesisTransaction.transactionId = "0";
        // Manually add the Transaction Output
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
        // Store our first transaction in the UTXOs list
        UTXOs.put(genesisTransaction.outputs.get(0).getId(), genesisTransaction.outputs.get(0));

        LOGGER.log(Level.INFO, "Creating and Mining Genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
        
        // TESTING
        Block block1 = new Block(genesis.getHash());
        LOGGER.log(Level.INFO, () -> "walletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, "walletA is attempting to send funds (40) to walletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        LOGGER.log(Level.INFO, () -> "walletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, () -> "walletB's balance is: " + walletB.getBalance());
        
        Block block2 = new Block(block1.getHash());
        LOGGER.log(Level.INFO, "WalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(block2);
        LOGGER.log(Level.INFO, () -> "walletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, () -> "walletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        LOGGER.log(Level.INFO, "WalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        LOGGER.log(Level.INFO, () -> "walletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, () -> "walletB's balance is: " + walletB.getBalance());
        
        isChainValid();
        
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        
        //A temporary working list of unspent transactions at a given block state
        Map<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); 
        tempUTXOs.put(genesisTransaction.outputs.get(0).getId(), genesisTransaction.outputs.get(0));

        //Loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            
            //Compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                LOGGER.log(Level.WARNING, "Current Hashes not equal");
                return false;
            }

            //Compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                LOGGER.log(Level.WARNING, "Previous Hashes not equal");
                return false;
            }
            
            //Check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                LOGGER.log(Level.WARNING, "This block hasn't been mined");
                return false;
            }

            //Loop through blockchains transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if (!currentTransaction.verifySignature()) {
                    LOGGER.log(Level.WARNING, "Signature on Transaction {} is Invalid", t);
                    return false;
                }
                
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    LOGGER.log(Level.WARNING, "Inputs are note equal to outputs on Transaction {}", t);
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        LOGGER.log(Level.WARNING, "Referenced input on Transaction {} is Missing", t);
                        return false;
                    }

                    if (input.getUTXO().getValue() != tempOutput.getValue()) {
                        LOGGER.log(Level.WARNING, "Referenced input Transaction {} value is Invalid", t);
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTransaction.outputs.get(0).getRecipient() != currentTransaction.recipient) {
                    LOGGER.log(Level.WARNING, "Transaction {} output recipient is not who it should be", t);
                    return false;
                }
                
                if (currentTransaction.outputs.get(1).getRecipient() != currentTransaction.sender) {
                    LOGGER.log(Level.WARNING, "Transaction {} output 'change' is not sender.", t);
                    return false;
                }
            }
        }
        LOGGER.log(Level.INFO, "Blockchain is valid");
        return true;
    }
    
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}

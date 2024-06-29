package dev.asjordi.util;

import com.google.gson.*;
import dev.asjordi.model.Transaction;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * The StringUtil class provides a set of utility methods for working with Strings and cryptographic operations.
 * This class cannot be instantiated and all its methods are static.
 * @author Jordi <ejordi.ayala@gmail.com>
 */
public class StringUtil {

    /**
     * Private constructor to prevent instantiation of the class.
     * @throws IllegalStateException
     */
    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Applies SHA-256 algorithm to a given input and returns the result as a String.
     * @param input The String to which the SHA-256 algorithm will be applied.
     * @return The result of applying the SHA-256 algorithm to the input as a String.
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8")); //Applies SHA256 to input
            StringBuilder hexString = new StringBuilder(); // Contains hash as HEX
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Applies ECDSA Signature and returns the result as bytes.
     * @param privateKey The private key to sign.
     * @param input The string to sign.
     * @return An array of bytes representing the signature.
     */
    public static byte[] applyECDSASignature(PrivateKey privateKey,String input) {
        Signature dsa = null;
        byte[] output = new byte[0];
        
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return output;
    }
    
    /**
     * Take a signature, a public key, and data in string form and validates the signature.
     * @param publicKey The public key to verify the signature.
     * @param data The data that were signed.
     * @param signature The signature to verify.
     * @return True if the signature is valid, false otherwise.
     */
    public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Returns an encoded String from any Key.
     * @param key The key to encode.
     * @return A String representing the encoded key.
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Converts an object into a JSON String.
     * @param o The object to convert into a JSON String.
     * @return A string representing the object in JSON format.
     */
    public static String getJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }
    
    /**
     * Helper method to generate a Merkle Root.
     * @param transactions A list of transactions to calculate the Merkle Root.
     * @return The Merkle Root of the provided transactions.
     */
    public static String getMerkleRoot(List<Transaction> transactions) {
        
        int count = transactions.size();
        List<String> previousTreeLayer = new LinkedList<>();
        for (Transaction t : transactions) {
            previousTreeLayer.add(t.transactionId);
        }
        
        List<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new LinkedList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
    
    /**
     * Returns a difficulty target string, to compare with the hash. For example, a difficulty of 5 will return "00000".
     * @param difficulty The target difficulty.
     * @return A string representing the target difficulty.
     */
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}

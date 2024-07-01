package dev.asjordi;

import dev.asjordi.model.Transaction;
import dev.asjordi.model.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private Wallet walletA;
    private Wallet walletB;

    @BeforeAll
    public static void setUpAll() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @BeforeEach
    public void setUp() {
        walletA = new Wallet();
        walletB = new Wallet();
    }

    @Test
    void testKeyPairGeneration() {
        assertNotNull(walletA.getPublicKey(), "Public key should not be null");
        assertNotNull(walletA.getPrivateKey(), "Private key should not be null");
    }

    @Test
    void testBalanceOfNewWallet() {
        assertEquals(0, walletA.getBalance(), "Balance should be 0");
    }

    @Test
    void testVerifySignatureFromTransaction() {
        Transaction t = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        t.generateSignature(walletA.getPrivateKey());
        assertTrue(t.verifySignature(), "Signature should be verified");
    }
}

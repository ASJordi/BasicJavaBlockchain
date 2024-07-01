package dev.asjordi;

import dev.asjordi.model.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockTest {

    private static Block block1;
    private static List<Block> blockchain;
    private static Block block2;
    private static Block block3;
    private static int difficulty;

    @BeforeAll
    static void setUp() {
        blockchain = new LinkedList<>();
        difficulty = 3;

        block1 = new Block("0");
        blockchain.add(block1);
        blockchain.getFirst().mineBlock(difficulty);

        block2 = new Block(blockchain.get(blockchain.size() - 1).getHash());
        blockchain.add(block2);
        blockchain.get(1).mineBlock(difficulty);

        block3 = new Block(blockchain.get(blockchain.size() - 1).getHash());
        blockchain.add(block3);
        blockchain.get(2).mineBlock(difficulty);
    }

    @Test
    void testBlockchainSize() {
        assertEquals(3, blockchain.size(), "Blockchain size should be 3");
    }

    @Test
    void testPreviousHashesFromBlocks() {
        assertEquals("0", block1.getPreviousHash(), "First previous block hash should be \"0\"");
        assertEquals(block1.getHash(), block2.getPreviousHash(), "Second previous block hash should be " + block1.getHash());
        assertEquals(block2.getHash(), block3.getPreviousHash(), "Third previous block hash should be " + block2.getHash());
    }
}

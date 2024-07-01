package dev.asjordi;

import dev.asjordi.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StringUtilTest {

    @Test
    void testApplySha256() {
        String output = StringUtil.applySha256("test");
        assertNotNull(output);
    }

    @Test
    void testGetJson() {
        Object o = new Object();
        String json = StringUtil.getJson(o);
        assertNotNull(json);
    }

    @Test
    void testGetDifficultyString() {
        String difficultyString = StringUtil.getDifficultyString(5);
        assertNotNull(difficultyString);
        assertEquals("00000", difficultyString);
    }

}
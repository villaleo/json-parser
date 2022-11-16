package tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class TestJsonParser {
    @Test
    public void testHappyPath() {
        var filename = "src/test/resources/happyPath.json";
        JsonParser parser;

        try {
            parser = new JsonParser(filename);
        } catch (FileNotFoundException err) {
            Assertions.fail(err.getMessage());
            return;
        }

        var map = parser.parseJsonToMap();
        Assertions.assertNotNull(map);
        Assertions.assertFalse(map.isEmpty());
    }

    @Test
    public void testInvalidType() {
        var filename = "src/test/resources/invalidType.json";
        JsonParser parser;

        try {
            parser = new JsonParser(filename);
        } catch (FileNotFoundException err) {
            Assertions.fail(err.getMessage());
            return;
        }

        var map = parser.parseJsonToMap();
        Assertions.assertNull(map);
    }
}

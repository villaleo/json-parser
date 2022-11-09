import tools.JsonParser;

import java.io.FileNotFoundException;

public class Main {
    public static final String FILE = "src/main/resources/input/jsonBasicInputNoObjects.json";

    public static void main(String[] args) {
        JsonParser parser;
        try {
            parser = new JsonParser(FILE);
        } catch (FileNotFoundException err) {
            System.out.printf("File not found: %s", err.getMessage());
            return;
        } finally {
            System.out.printf("Opened file `%s`.\n", FILE);
        }

        var map = parser.parseJsonToMap();
        System.out.println(map);
    }
}

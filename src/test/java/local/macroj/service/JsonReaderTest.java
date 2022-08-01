package local.macroj.service;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void checkThatParseReturnsValidMap() throws IOException {
        JsonReader reader = new JsonReader("mapping-eso.json");
        Map<String, JsonElement> map = reader.parse();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("F13"));
    }
}
package local.macroj.utils;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
class YamlJsonConverterTest {

    @Test
    void convertYamlComplexToJson() throws IOException, JSONException {
        String input = readFile("mapping-complex.yaml");
        String expected = readFile("mapping-complex.json");

        YamlJsonConverter converter = new YamlJsonConverter();

        JSONAssert.assertEquals(expected, converter.toJson(input), false);
    }

    @Test
    void convertYamlSimpleToJson() throws IOException, JSONException {
        String input = readFile("mapping-simple.yaml");
        String expected = readFile("mapping-simple.json");

        YamlJsonConverter converter = new YamlJsonConverter();

        JSONAssert.assertEquals(expected, converter.toJson(input), false);
    }

    @Test
    void convertJsonToYaml() throws IOException, JSONException {
        String input = readFile("mapping-simple.json");
        String expected = readFile("mapping-simple.yaml");

        YamlJsonConverter converter = new YamlJsonConverter();

        //workaround
        Assertions.assertEquals(
                expected.replaceAll("[\\s|\"']+", ""),
                converter.toYaml(input).replaceAll("[\\s|\"']+", ""),
                "convertJsonSimpleToYamlSimple"
        );
    }

    private String readFile(String fileName) throws IOException {
        try (java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return new String(stream.readAllBytes());
        }
    }
}
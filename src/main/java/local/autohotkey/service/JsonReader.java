package local.autohotkey.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import local.autohotkey.utils.Files;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonReader {

    private String filePath;

    public JsonReader(String path) {
        this.filePath = path;
    }

    public Map<String, JsonElement> parse() throws IOException {
        Gson gson = new Gson();
        Type empMapType = new TypeToken<Map<String, JsonElement>>() {
        }.getType();
        InputStream is = Files.loadResource(filePath);

        Map<String, JsonElement> data = gson.fromJson(CharStreams.toString(new InputStreamReader(
                is, Charsets.UTF_8)), empMapType);
        is.close();
        return data;
    }
}

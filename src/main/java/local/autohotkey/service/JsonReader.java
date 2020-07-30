package local.autohotkey.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
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
        InputStream is = loadResource();

        Map<String, JsonElement> data = gson.fromJson(CharStreams.toString(new InputStreamReader(
                is, Charsets.UTF_8)), empMapType);
        is.close();
        return data;
    }

    private InputStream loadResource() throws IOException {
        File localFile = new File("./" + filePath);
        InputStream is;
        if(localFile.exists()) {
            is = new FileInputStream(localFile);
        } else {
            is =  new ClassPathResource(filePath).getInputStream();
        }
        return is;
    }
}

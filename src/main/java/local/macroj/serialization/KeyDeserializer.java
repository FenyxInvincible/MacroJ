package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.macroj.data.Key;
import local.macroj.service.KeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
public class KeyDeserializer implements JsonDeserializer<Key> {

    private final KeyManager keys;
    @Value("${app.config.virtualKeyPrefix}")
    private String virtualKeyPrefix;

    @Override
    public Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String keyName = json.getAsString();

        Key key = keys.findKeyByTextSafe(keyName);
        if(key == null) {
            if(keyName.toUpperCase().startsWith(virtualKeyPrefix.toUpperCase())) {
                key = keys.createVirtualKey(keyName.toUpperCase());
            } else {
                throw new JsonParseException("Cannot parse " + json.getAsString() + " to Key");
            }
        }
        return key;
    }
}

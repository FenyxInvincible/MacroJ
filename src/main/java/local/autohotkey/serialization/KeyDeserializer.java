package local.autohotkey.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.autohotkey.data.Key;
import local.autohotkey.service.KeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
public class KeyDeserializer implements JsonDeserializer<Key> {

    private final KeyManager keys;

    @Override
    public Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Key key = keys.findKeyByText(json.getAsString());
        if(key == null) {
            throw new JsonParseException("Cannot parse " + json.getAsString() + " to Key");
        }
        return key;
    }
}

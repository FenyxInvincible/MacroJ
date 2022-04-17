package local.autohotkey.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.service.KeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
public class MacroKeyDeserializer implements JsonDeserializer<MacroKey> {

    private final KeyManager keys;

    @Override
    public MacroKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String[] ks = json.getAsString().split("\\+");

        String keyString = ks.length <= 1 ? ks[0] : ks[1];
        String modifierString = ks.length <= 1 ? null : ks[0];

        Key key = keys.findKeyByText(keyString);
        Key modifier = modifierString != null ? keys.findKeyByText(modifierString) : null;

        if(key == null) {
            throw new JsonParseException("Cannot parse " + json.getAsString() + " to Key");
        }
        return new MacroKey(key, modifier);
    }
}

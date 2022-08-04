package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.macroj.data.Key;
import local.macroj.data.MacroKey;
import local.macroj.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
@Slf4j
public class MacroKeyDeserializer implements JsonDeserializer<MacroKey> {

    private final KeyManager keys;

    @Value("${app.config.virtualKeyPrefix}")
    private String virtualKeyPrefix;

    @Override
    public MacroKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String[] ks = json.getAsString().split("\\+");

        String keyString = ks.length <= 1 ? ks[0] : ks[1];
        String modifierString = ks.length <= 1 ? null : ks[0];

        Key key = keys.findKeyByTextSafe(keyString);
        Key modifier = modifierString != null ? keys.findKeyByText(modifierString) : null;

        if (key == null) {
            if (keyString.toUpperCase().startsWith(virtualKeyPrefix.toUpperCase())) {
                key = keys.createVirtualKey(keyString.toUpperCase());
                log.info("Created virtual key: {}", key);
            } else {
                throw new JsonParseException("Cannot parse " + json.getAsString() + " to Key");
            }
        }
        return new MacroKey(key, modifier);
    }
}

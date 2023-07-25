package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.macroj.data.MacroBaseActionData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class MacroBaseActionDataDeserializer implements JsonDeserializer<MacroBaseActionData> {

    @Override
    public MacroBaseActionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var data = json.getAsJsonObject();
        AtomicBoolean isUseKey = new AtomicBoolean(false);
        //as it's executed only when profile is loaded we can sacrifice speed to flexibility
        data.entrySet().forEach(e -> {
            if (e.getKey().equalsIgnoreCase("key")) {
                isUseKey.set(true);
            }
        });

        return (isUseKey.get()) ? new UseKeyDataDeserializer().deserialize(json, typeOfT, context)
                : new MouseMoveDataDeserializer().deserialize(json, typeOfT, context);
    }
}

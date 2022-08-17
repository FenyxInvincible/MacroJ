package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ColorDeserializer implements JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var data = json.getAsJsonObject();

        var r = Optional.ofNullable(
            Optional.ofNullable(data.get("r")).orElseGet(() -> data.get("R"))
        ).orElseThrow(() -> new IllegalArgumentException("Parameter 'r' is not found. r,g,b parameters have to be provided")).getAsInt();

        var g = Optional.ofNullable(
            Optional.ofNullable(data.get("g")).orElseGet(() -> data.get("G"))
        ).orElseThrow(() -> new IllegalArgumentException("Parameter 'g' is not found. r,g,b parameters have to be provided")).getAsInt();

        var b = Optional.ofNullable(
            Optional.ofNullable(data.get("b")).orElseGet(() -> data.get("B"))
        ).orElseThrow(() -> new IllegalArgumentException("Parameter 'b' is not found. r,g,b parameters have to be provided")).getAsInt();

        return new Color(r, g, b);
    }
}

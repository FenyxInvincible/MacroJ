package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.macroj.data.MouseMoveData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MouseMoveDataDeserializer implements JsonDeserializer<MouseMoveData> {

    @Override
    public MouseMoveData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var data = json.getAsJsonObject();
        final Object[] params = new Object[4];

        data.entrySet().forEach(e -> {
            if (e.getKey().equalsIgnoreCase("x")) {
                //Variable used in lambda expression should be final or effectively final
                params[0] = context.deserialize(e.getValue(), Integer.class);
            }

            if (e.getKey().equalsIgnoreCase("y")) {
                //Variable used in lambda expression should be final or effectively final
                params[1] = context.deserialize(e.getValue(), Integer.class);
            }

            if (e.getKey().equalsIgnoreCase("delay")) {
                var delayAsString = e.getValue().getAsString();
                if (delayAsString.contains("-")) {
                    var split = delayAsString.split("-");
                    int minimum = Integer.parseInt(split[0]);
                    int maximum = Integer.parseInt(split[1]);
                    Random rn = new Random();
                    int range = maximum - minimum + 1;
                    params[2] = rn.nextInt(range) + minimum;
                } else {
                    params[2] = (int)(Double.parseDouble(e.getValue().getAsString()));
                }
            }

            if (e.getKey().equalsIgnoreCase("movement")) {
                params[3] = context.deserialize(e.getValue(), MouseMoveData.MouseMovementType.class);
            }
        });

        var builder = MouseMoveData.builder();
        //defaults
        if (params[2] != null) {
            builder.delay((Integer) params[2]);
        }
        if (params[3] != null) {
            builder.movement((MouseMoveData.MouseMovementType) params[3]);
        }

        return builder.x((Integer) params[0]).y((Integer)params[1]).build();
    }
}

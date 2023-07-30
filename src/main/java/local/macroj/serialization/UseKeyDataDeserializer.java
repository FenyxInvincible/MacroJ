package local.macroj.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import local.macroj.data.Key;
import local.macroj.data.UseKeyData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class UseKeyDataDeserializer implements JsonDeserializer<UseKeyData> {

    @Override
    public UseKeyData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var data = json.getAsJsonObject();
        final Object[] params = new Object[3];

        data.entrySet().forEach(e -> {
            if (e.getKey().equalsIgnoreCase("key")) {
                //Variable used in lambda expression should be final or effectively final
                params[0] = context.deserialize(e.getValue(), Key.class);
            }

            if (e.getKey().equalsIgnoreCase("delay")) {
                var delayAsString = e.getValue().getAsString();
                if (delayAsString.contains("-")) {
                    var split = delayAsString.split("-");
                    int minimum = Integer.parseInt(split[0]);
                    int maximum = Integer.parseInt(split[1]);
                    Random rn = new Random();
                    int range = maximum - minimum + 1;
                    params[1] = rn.nextInt(range) + minimum;
                } else {
                    params[1] = (int)(Double.parseDouble(e.getValue().getAsString()));
                }
            }

            if (e.getKey().equalsIgnoreCase("action")) {
                params[2] = context.deserialize(e.getValue(), Key.Action.class);
            }
        });

        var builder = UseKeyData.builder();
        //defaults
        if (params[1] != null) {
            builder.delay((Integer) params[1]);
        }
        if (params[2] != null) {
            builder.action((Key.Action) params[2]);
        }


        return builder.key((Key) params[0]).build();
    }
}

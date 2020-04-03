package local.autohotkey.data;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
@RequiredArgsConstructor
public class Key {
    private final int keyCode;
    private final String keyText;
    private AtomicBoolean isPressed = new AtomicBoolean(false);

    public static Key create(JsonObject jsonElement) {
        return new Key(
                jsonElement.get("keyCode").getAsInt(),
                jsonElement.get("keyText").getAsString()
        );
    }

    public void released() {
        isPressed.set(false);
    }

    public void pressed() {
        isPressed.set(true);
    }

    public boolean isPressed(){
        return isPressed.get();
    }
}

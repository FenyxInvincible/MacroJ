package local.autohotkey.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import local.autohotkey.key.MouseKey;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@RequiredArgsConstructor
public class Key {
    private final int keyCode;
    private final String keyText;
    private final int scanCode;
    /**
     * Some keys don't have usual press release flow, such as Mouse Scroll may have Press and Release independently
     */
    private final boolean hasPressReleaseFlow;

    @Expose
    private AtomicBoolean isPressed = new AtomicBoolean(false);

    /**
     * todo refactor it to save this information in keys.json instead of hardcode
     *
     * arrays of vCodes which don't have usual behaviour as press/release. As example scroll which may send up and down independently
     */
    private final int[] singleStateKeys = new int[]{1986};

    public static Key create(JsonObject jsonElement) {
        return new Key(
                jsonElement.get("keyCode").getAsInt(),
                jsonElement.get("keyText").getAsString(),
                jsonElement.get("scanCode").getAsInt(),
                jsonElement.get("hasPressReleaseFlow").getAsBoolean()
        );
    }

    public void released() {
        if(hasPressReleaseFlow) {
            isPressed.set(false);
        }
    }

    public void pressed() {
        if(hasPressReleaseFlow) {
            isPressed.set(true);
        }
    }

    public boolean isPressed(){
        if(hasPressReleaseFlow) {
            return isPressed.get();
        } else {
            return false;
        }
    }

    public boolean isMouseKey() {
        return MouseKey.of(keyText) != MouseKey.UNKNOWN;
    }


    @Nullable
    public enum Action {
        Press,
        Release,
        Send;

        public static Action of(String str) {
            return Arrays.stream(Action.values())
                    .filter(e -> e.name().equalsIgnoreCase(str))
                    .findAny().orElse(null);
        }
    }

    @Override
    public String toString() {
        return "Key{" +
                "keyCode=" + keyCode +
                ", keyText='" + keyText + '\'' +
                ", scanCode=" + scanCode +
                '}';
    }
}

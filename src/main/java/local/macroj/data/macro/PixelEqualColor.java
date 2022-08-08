package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.data.MacroKey;
import local.macroj.sender.Sender;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Type;

@Slf4j
@Component
@Scope("prototype")
public class PixelEqualColor extends PixelSame {

    private PixelEqualDesiredData data;

    public PixelEqualColor(Sender sender) {
        super(sender);
    }

    @Override
    public Type getParamsType() {
        return TypeToken.get(PixelEqualDesiredData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        this.data = (PixelEqualDesiredData)param;
        this.self = self;
    }

    @SneakyThrows
    @Override
    public void run() {
        int attempts = data.getMaxAttempts();
        checking(attempts, data.getDesiredColor());
    }

    @Data()
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PixelEqualDesiredData extends PixelSame.PixelEqualData {
        @NonNull
        private Color desiredColor;
    }
}

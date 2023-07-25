package local.macroj.data;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MacroBaseActionData {
    @Builder.Default
    private int delay = 32;
}

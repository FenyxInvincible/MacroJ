package local.macroj.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MouseMoveData extends MacroBaseActionData {
    @NonNull
    private Integer x;
    @NonNull
    private Integer y;

    @Builder.Default
    MouseMovementType movement = MouseMovementType.Absolute;

    @Nullable
    public enum MouseMovementType {
        Absolute,
        Relative
    }
}


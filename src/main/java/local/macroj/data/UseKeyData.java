package local.macroj.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UseKeyData extends MacroBaseActionData {
    @NonNull
    private Key key;

    @Builder.Default
    private Key.Action action = Key.Action.Send;
}

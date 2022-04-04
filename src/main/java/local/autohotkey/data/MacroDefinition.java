package local.autohotkey.data;

import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Data
public class MacroDefinition {
    MacroDefinitionAction onPress;
    MacroDefinitionAction onRelease;
}

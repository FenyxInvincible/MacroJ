package local.autohotkey.data;

import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Data
public class MacroDefinition {
    /**
     * Used, if we want to restrict macro only by foreground application
     */
    ApplicationDefinition application;
    MacroDefinitionAction onPress;
    MacroDefinitionAction onRelease;
}

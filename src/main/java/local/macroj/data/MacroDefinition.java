package local.macroj.data;

import lombok.Data;

@Data
public class MacroDefinition {
    /**
     * Used, if we want to restrict macro only by foreground application
     */
    ApplicationDefinition application;
    MacroDefinitionAction onPress;
    MacroDefinitionAction onRelease;
}

package local.macroj.data;

import lombok.Value;

@Value
public class MacroDefinitionAction {
    /**
     * Class for execution
     */
    String macroClass;
    /**
     * how params will be parsed to
     */
    Object params;
}

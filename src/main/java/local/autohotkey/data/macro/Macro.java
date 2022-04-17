package local.autohotkey.data.macro;


import local.autohotkey.data.MacroKey;

import java.lang.reflect.Type;

public interface Macro extends Runnable {
    void setParams(Object param, MacroKey self);
    default Type getParamsType(){
        return null;
    };
}

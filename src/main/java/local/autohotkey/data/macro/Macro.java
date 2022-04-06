package local.autohotkey.data.macro;


import local.autohotkey.data.Key;

import java.lang.reflect.Type;

public interface Macro extends Runnable {
    void setParams(Object param, Key self);
    default Type getParamsType(){
        return null;
    };
}

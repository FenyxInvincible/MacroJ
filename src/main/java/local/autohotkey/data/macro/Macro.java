package local.autohotkey.data.macro;


import local.autohotkey.data.Key;

public interface Macro extends Runnable {
    void setParams(Object param, Key self);

    default Class<?> getParamsType(){
        return null;
    };
}

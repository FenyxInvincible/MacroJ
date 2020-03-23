package local.autohotkey.data.macro;


import java.util.List;

public interface Macro extends Runnable {
    void setParams(List<String> params);
}

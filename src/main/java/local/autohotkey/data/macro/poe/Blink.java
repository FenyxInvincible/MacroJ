package local.autohotkey.data.macro.poe;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Blink implements Macro {
    private final Sender sender;
    private final KeyManager keys;
    private final Locks locks;

    private static Key key;

    @Override
    public void setParams(List<String> params) {
    }
//2002 1383
    @Override
    public void run() {
        int color = ScreenPicker.pickDwordColor(2002, 1383);
        long time = System.currentTimeMillis();
        sender.sendKey(getKey(keys), 50);

        while (color == ScreenPicker.pickDwordColor(2002, 1383) && System.currentTimeMillis() - time < 1500) {
            try {
                sender.sendKey(getKey(keys), 50);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static Key getKey(KeyManager keys){
        if (key == null) {
            key = keys.findKeyByText("4");
        }
        return key;
    }
}

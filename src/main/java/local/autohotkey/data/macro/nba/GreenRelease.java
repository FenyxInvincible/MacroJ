package local.autohotkey.data.macro.nba;

import local.autohotkey.data.Key;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GreenRelease implements Macro {

    private final Sender sender;
    private final KeyManager keys;

    private static Key key;
    private int delay;

    @Override
    public void setParams(List<String> params) {
        key = keys.findKeyByText("NUMPAD5");
        delay = Integer.parseInt(params.get(0));
    }
    //2002 1383
    @Override
    public void run() {
        long start = System.currentTimeMillis();

        sender.pressKey(key);

        while (System.currentTimeMillis() - start < delay) {
            //do nothing
        }
        sender.releaseKey(key);
    }
}
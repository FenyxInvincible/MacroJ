package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.key.MouseKey;
import local.autohotkey.sender.RobotSender;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.ScreenPicker;
import local.autohotkey.utils.eso.EsoUtils;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class Fallout76Vats implements Macro {

    private static final int POTION_COLOR = 7165462;
    private static final int SYNERGY_COLOR = 16777215;
    private static final long SWITCH_DELAY = 200;
    private final RobotSender sender;
    private final KeyManager keys;
    private final Locks locks;
    private Key yKey;
    private Key space;
    private Key tab;
    public static final int DWORD_VATS_COLOR = 13369343;
    public static final int DWORD_CRIT_COLOR = 13369343;


    @PostConstruct
    public void init(){
        yKey = keys.findKeyByText("Y");
        space = keys.findKeyByText("SPACE");
        tab = keys.findKeyByText("TAB");
    }

    @Override
    public void setParams(List<String> params) {

    }

    @Override
    public void run() {
        try {
            log.info("Send {}", yKey);
            sender.sendKey(yKey, 45);
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < 500) {
                int vatsColor = ScreenPicker.pickDwordColor(889, 1375);
                if(vatsColor == DWORD_VATS_COLOR){
                    Thread.sleep(30);
                    log.info("Send {}", tab);
                    sender.sendKey(tab, 45);
                    log.info("Send {}", yKey);
                    Thread.sleep(30);
                    sender.sendKey(yKey, 45);

                    if(ScreenPicker.pickDwordColor(1606, 1380) == DWORD_VATS_COLOR) {
                        Thread.sleep(50);
                        sender.sendKey(space, 45);
                    }
                    break;
                }
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

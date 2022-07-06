package local.autohotkey.service;

import local.autohotkey.jna.hook.key.KeyEventReceiver;
import local.autohotkey.jna.hook.mouse.struct.MOUSEHOOKSTRUCT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import local.autohotkey.jna.Mouse;
import local.autohotkey.jna.hook.mouse.MouseEventReceiver;
import local.autohotkey.jna.hook.mouse.MouseHookManager;
import local.autohotkey.jna.hook.mouse.struct.MouseButtonType;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class MouseEventListener extends MouseEventReceiver {

    @Getter
    private static final AtomicBoolean isLocked = new AtomicBoolean(false);
    @Getter
    private static volatile Point lockedPoint = new Point(0, 0);
    private final MacroListener macroListener;

    public MouseEventListener(MouseHookManager hookManager, MacroListener macroListener) {
        super(hookManager);
        this.macroListener = macroListener;
    }

    @Override
    public boolean onMousePress(MouseButtonType mouseButtonType, MOUSEHOOKSTRUCT info) {
        boolean rtn = macroListener.onUpdate(
                MacroListener.EventState.DOWN,
                info,
                getFakeMouseKey(mouseButtonType)
        );
        return rtn;
    }

    @Override
    public boolean onMouseRelease(MouseButtonType mouseButtonType, MOUSEHOOKSTRUCT info) {
        return macroListener.onUpdate(
                MacroListener.EventState.UP,
                info,
                getFakeMouseKey(mouseButtonType)
        );
    }

    private int getFakeMouseKey(MouseButtonType mouseButtonType) {
        if(mouseButtonType.equals(MouseButtonType.RIGHT_DOWN) || mouseButtonType.equals(MouseButtonType.RIGHT_UP)) {
            return FakeMouseKeys.RMB.getValue();
        }
        if(mouseButtonType.equals(MouseButtonType.MIDDLE_DOWN) || mouseButtonType.equals(MouseButtonType.MIDDLE_UP)) {
            return FakeMouseKeys.MMB.getValue();
        }
        return FakeMouseKeys.LMB.getValue();
    }

    @Override
    public boolean onMouseScroll(boolean isDown, MOUSEHOOKSTRUCT info) {
        log.debug("Scroll is found: isDown {}, point {}", isDown, info.pt);
        boolean rtn = macroListener.onUpdate(
                isDown ? MacroListener.EventState.DOWN : MacroListener.EventState.UP,
                info,
                FakeMouseKeys.MOUSE_SCROLL.getValue()
        );
        return rtn;
    }

    @Override
    public boolean onMouseMove(MOUSEHOOKSTRUCT info) {
        if (isLocked.get()) {
            Mouse.mouseMove(info.pt.x, info.pt.y);
            return false;
        } else {
            return false;
        }
    }

    private enum FakeMouseKeys {
        //These fake keys HAS to be registered in keys.json with same values
        LMB (1983), RMB(1984), MMB(1985), MOUSE_SCROLL(1986);
        private final int value;

        FakeMouseKeys(int enumVal) {
            this.value = enumVal;
        }

        int getValue() {
            return value;
        }
    }
}

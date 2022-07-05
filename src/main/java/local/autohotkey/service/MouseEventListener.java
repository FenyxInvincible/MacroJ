package local.autohotkey.service;

import com.sun.jna.platform.win32.WinDef;
import local.autohotkey.jna.hook.key.KeyEventReceiver;
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

    @Getter
    private static final AtomicBoolean isLocked = new AtomicBoolean(false);
    @Getter
    private static volatile Point lockedPoint = new Point(0, 0);
    private final MacroKeyListener macroKeyListener;

    public MouseEventListener(MouseHookManager hookManager,  MacroKeyListener macroKeyListener) {
        super(hookManager);
        this.macroKeyListener = macroKeyListener;
    }

    @Override
    public boolean onMousePress(MouseButtonType mouseButtonType, WinDef.HWND hwnd, WinDef.POINT point) {
        boolean rtn = macroKeyListener.onKeyUpdate(KeyEventReceiver.PressState.DOWN, getFakeMouseKey(mouseButtonType));
        return rtn;
    }

    @Override
    public boolean onMouseRelease(MouseButtonType mouseButtonType, WinDef.HWND hwnd, WinDef.POINT point) {
        return macroKeyListener.onKeyUpdate(KeyEventReceiver.PressState.UP, getFakeMouseKey(mouseButtonType));
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
    public boolean onMouseScroll(boolean isDown, WinDef.HWND hwnd, WinDef.POINT point) {
        boolean rtn = macroKeyListener.onKeyUpdate(
                isDown ? KeyEventReceiver.PressState.DOWN : KeyEventReceiver.PressState.UP,
                FakeMouseKeys.MOUSE_SCROLL.getValue()
        );
        return rtn;
    }

    @Override
    public boolean onMouseMove(WinDef.HWND hwnd, WinDef.POINT point) {
        if (isLocked.get()) {
            Mouse.mouseMove(point.x, point.y);
            return false;
        } else {
            return false;
        }
    }
}

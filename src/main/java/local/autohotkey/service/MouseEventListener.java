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
        return macroKeyListener.onKeyUpdate(KeyEventReceiver.PressState.DOWN, getFakeMouseKey(mouseButtonType));
    }

    @Override
    public boolean onMouseRelease(MouseButtonType mouseButtonType, WinDef.HWND hwnd, WinDef.POINT point) {
        return macroKeyListener.onKeyUpdate(KeyEventReceiver.PressState.UP, getFakeMouseKey(mouseButtonType));
    }

    private int getFakeMouseKey(MouseButtonType mouseButtonType) {
        if(mouseButtonType.equals(MouseButtonType.RIGHT_DOWN) || mouseButtonType.equals(MouseButtonType.RIGHT_UP)) {
            return 1984;
        }
        if(mouseButtonType.equals(MouseButtonType.MIDDLE_DOWN) || mouseButtonType.equals(MouseButtonType.MIDDLE_UP)) {
            return 1985;
        }
        return 1983;
    }

    @Override
    public boolean onMouseScroll(boolean b, WinDef.HWND hwnd, WinDef.POINT point) {
        return false;
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

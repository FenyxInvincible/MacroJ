package local.autohotkey.service;

import com.sun.jna.platform.win32.WinDef;
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

    public MouseEventListener(MouseHookManager hookManager) {
        super(hookManager);
    }

    @Override
    public boolean onMousePress(MouseButtonType mouseButtonType, WinDef.HWND hwnd, WinDef.POINT point) {
        return false;
    }

    @Override
    public boolean onMouseRelease(MouseButtonType mouseButtonType, WinDef.HWND hwnd, WinDef.POINT point) {
        return false;
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

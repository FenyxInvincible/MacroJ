package local.macroj.jna.hook.mouse;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import local.macroj.jna.hook.DeviceEventReceiver;
import local.macroj.jna.hook.mouse.struct.LowLevelMouseProc;
import local.macroj.jna.hook.mouse.struct.MOUSEHOOKSTRUCT;
import local.macroj.jna.hook.mouse.struct.MouseButtonType;
import lombok.extern.slf4j.Slf4j;

/**
 * A simplified representation of a LowLevelMouseProc.
 * 
 * @author Matt
 */
@Slf4j
public abstract class MouseEventReceiver extends DeviceEventReceiver<MouseHookManager> implements LowLevelMouseProc {
	public static final int WM_MOUSEMOVE = 512;
	public static final int WM_MOUSESCROLL = 522;
	public static final int WM_MOUSELDOWN = 513, WM_MOUSELUP = 514;
	public static final int WM_MOUSEMDOWN = 519, WM_MOUSEMUP = 520;
	public static final int WM_MOUSERDOWN = 516, WM_MOUSERUP = 517;

	public MouseEventReceiver(MouseHookManager hookManager) {
		super(hookManager);
	}

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT info) {
		boolean cancel = false;

		int code = wParam.intValue();

		if (code == WM_MOUSEMOVE) {
			cancel = onMouseMove(info);
		} else if (code == WM_MOUSESCROLL) {
			boolean down = Pointer.nativeValue(info.hwnd.getPointer()) == 4287102976L;
			cancel = onMouseScroll(down, info);
		} else if (code == WM_MOUSELDOWN || code == WM_MOUSERDOWN || code == WM_MOUSEMDOWN) {
			cancel = onMousePress(MouseButtonType.fromWParam(code), info);
		} else if (code == WM_MOUSELUP || code == WM_MOUSERUP || code == WM_MOUSEMUP) {
			cancel = onMouseRelease(MouseButtonType.fromWParam(code), info);
		}
		if (cancel) {
			return new LRESULT(1);
		}
		Pointer ptr = info.getPointer();
		long peer = Pointer.nativeValue(ptr);
		return User32.INSTANCE.CallNextHookEx(getHookManager().getHhk(this), nCode, wParam, new LPARAM(peer));
	}


	/**
	 * Called when the mouse is pressed. Returning true will cancel the event.
	 * 
	 * @param type
	 *            Type is press <i>(Left, Right, Middle)</i>
	 * @param info
	 *            Mouse information.
	 * @return Event cancellation
	 */
	public abstract boolean onMousePress(MouseButtonType type, MOUSEHOOKSTRUCT info);

	/**
	 * Called when the mouse is released. Returning true will cancel the event.
	 * 
	 * @param type
	 *            Type is press <i>(Left, Right, Middle)</i>
	 * @param info
	 *            Mouse information.
	 * @return Event cancellation
	 */
	public abstract boolean onMouseRelease(MouseButtonType type, MOUSEHOOKSTRUCT info);

	/**
	 * Called when the mouse wheel is moved. Returning true will cancel the event.
	 * 
	 * @param down
	 *            Scroll event is down <i>(Negative movement)</i>
	 * @param info
	 *            Mouse event information.
	 * @return Event cancellation
	 */
	public abstract boolean onMouseScroll(boolean down, MOUSEHOOKSTRUCT info);

	/**
	 * Called when the mouse wheel is moved. Returning true will cancel the event.
	 * 
	 * @param info
	 *            MOUSEHOOKSTRUCT struct information.
	 * @return Event cancellation
	 */
	public abstract boolean onMouseMove(MOUSEHOOKSTRUCT info);
}

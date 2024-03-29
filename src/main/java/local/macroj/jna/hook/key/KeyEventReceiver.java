package local.macroj.jna.hook.key;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import local.macroj.jna.hook.DeviceEventReceiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class KeyEventReceiver extends DeviceEventReceiver<KeyHookManager> implements LowLevelKeyboardProc {
	private static final int WM_KEYDOWN = 256, WM_KEYUP = 257;
	private static final int WM_SYSKEYDOWN = 260, WM_SYSKEYUP = 261;

	public KeyEventReceiver(KeyHookManager hookManager) {
		super(hookManager);
	}

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
		int code = wParam.intValue();
		boolean cancel = onKeyUpdate(SystemState.from(code), PressState.from(code), info, info.vkCode);
		if (cancel) {
			return new LRESULT(1);
		}

		Pointer ptr = info.getPointer();
		long peer = Pointer.nativeValue(ptr);

		log.debug("Callback: WPARAM {}, KBDLLHOOKSTRUCT {}}", wParam, info);

		if (info.flags == 0x0010 || info.flags == 0x0011) {
			info.flags = 0x0000;
		}

		if (info.flags == 0x0090 || info.flags == 0x0091) {
			info.flags = 0x0080;
		}

		if (info.flags != 0x0000 && info.flags != 0x0080) {
			log.warn("Possible keyboard LLKHF_INJECTED is found");
		}

		return User32.INSTANCE.CallNextHookEx(getHookManager().getHhk(this), nCode, wParam, new LPARAM(peer));
	}

	/**
	 * Called when a key-state is updated.
	 * 
	 * @param sysState
	 *            System <i>(alt key)</i> or standard state when event fired.
	 * @param pressState
	 *            Up or down state of event.
	 * @param time
	 *            Time of event.
	 * @param vkCode
	 *            Key-code.
	 * @return {@code true} if event is to be cancelled <i>(not handled by
	 *         windows)</i>. {@code false} if event is to be handled normally.
	 */
	public abstract boolean onKeyUpdate(SystemState sysState, PressState pressState, KBDLLHOOKSTRUCT time, int vkCode);

	/**
	 * System-key status.
	 * 
	 * @author Matt
	 */
	public enum SystemState {
		SYSTEM, STANDARD;

		public static SystemState from(int code) {
			return (code == WM_SYSKEYDOWN || code == WM_SYSKEYUP) ? SYSTEM : STANDARD;
		}
	}

	/**
	 * Up or down status.
	 * 
	 * @author Matt
	 */
	public enum PressState {
		UP, DOWN, UNKNOWN;

		public static PressState from(int code) {
			boolean up = code == WM_SYSKEYUP || code == WM_KEYUP;
			boolean down = code == WM_SYSKEYDOWN || code == WM_KEYDOWN;
			return (up) ? UP : (down) ? DOWN : UNKNOWN;
		}
	}
}

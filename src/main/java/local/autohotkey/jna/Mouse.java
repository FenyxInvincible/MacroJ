package local.autohotkey.jna;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinUser.INPUT;
import lombok.extern.slf4j.Slf4j;

import static local.autohotkey.jna.Windows.IS_MACRO;

/**
 * Mouse related methods and values.
 */
@Slf4j
public class Mouse {
	public static final int MOUSEEVENTF_MOVE = 1;
	public static final int MOUSEEVENTF_LEFTDOWN = 2;
	public static final int MOUSEEVENTF_LEFTUP = 4;
	public static final int MOUSEEVENTF_RIGHTDOWN = 8;
	public static final int MOUSEEVENTF_RIGHTUP = 16;
	public static final int MOUSEEVENTF_MIDDLEDOWN = 32;
	public static final int MOUSEEVENTF_MIDDLEUP = 64;

	public static final int MOUSEEVENTF_WHEEL = 0x0800;

	/**
	 * Moves the mouse relative to it's current position.
	 * 
	 * @param x
	 *            Horizontal movement
	 * @param y
	 *            Vertical movement
	 */
	public static void mouseMove(int x, int y) {
		mouseAction(x, y, MOUSEEVENTF_MOVE, false);
	}

	/**
	 * Sends a left-click input at the given x,y coordinates. If -1 is given for
	 * each of the inputs it will send the input to the current position of the
	 * mouse.
	 * 
	 * @param x
	 * @param y
	 */
	public static void mouseLeftClick(int x, int y, boolean allowRecursive) {
		mouseAction(x, y, MOUSEEVENTF_LEFTDOWN, allowRecursive);
		mouseAction(x, y, MOUSEEVENTF_LEFTUP, allowRecursive);
	}

	/**
	 * Sends a right-click input at the given x,y coordinates. If -1 is given for
	 * each of the inputs it will send the input to the current position of the
	 * mouse.
	 * 
	 * @param x
	 * @param y
	 */
	public static void mouseRightClick(int x, int y, boolean allowRecursive) {
		mouseAction(x, y, MOUSEEVENTF_RIGHTDOWN, allowRecursive);
		mouseAction(x, y, MOUSEEVENTF_RIGHTUP, allowRecursive);
	}

	/**
	 * Sends a middle-click input at the given x,y coordinates. If -1 is given for
	 * each of the inputs it will send the input to the current position of the
	 * mouse.
	 * 
	 * @param x
	 * @param y
	 */
	public static void mouseMiddleClick(int x, int y, boolean allowRecursive) {
		mouseAction(x, y, MOUSEEVENTF_MIDDLEDOWN, allowRecursive);
		mouseAction(x, y, MOUSEEVENTF_MIDDLEUP, allowRecursive);
	}

	/**
	 * Sends an action (flags) at the given x,y coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param flags
	 */
	public static void mouseAction(int x, int y, int flags, boolean allowRecursive) {
		mouseAction(x,y,flags, DwData.ZERO.value, allowRecursive);
	}
	public static void mouseAction(int x, int y, int flags, int dwData, boolean allowRecursive) {
		INPUT input = new INPUT();

		input.type = new DWORD(INPUT.INPUT_MOUSE);


		input.input.setType("mi");
		if (x != -1) {
			input.input.mi.dx = new LONG(x);
		}
		if (y != -1) {
			input.input.mi.dy = new LONG(y);
		}
		input.input.mi.time = new DWORD(0);
		input.input.mi.dwExtraInfo = new ULONG_PTR(allowRecursive ? 0 : IS_MACRO);
		input.input.mi.dwFlags = new DWORD(flags);
		input.input.mi.mouseData = new DWORD(dwData);
		DWORD amount = User32.INSTANCE.SendInput(new DWORD(1), new INPUT[] { input }, input.size());
		log.debug("Successful sendings {}", amount);
	}

	/**
	 * If dwFlags contains MOUSEEVENTF_WHEEL, then dwData specifies the amount of wheel movement.
	 * A positive value indicates that the wheel was rotated forward, away from the user;
	 * a negative value indicates that the wheel was rotated backward, toward the user.
	 * One wheel click is defined as WHEEL_DELTA, which is 120.
	 *
	 * If dwFlags is not MOUSEEVENTF_WHEEL, MOUSEEVENTF_XDOWN, or MOUSEEVENTF_XUP, then dwData should be zero.
	 */
	public enum DwData {
		ZERO(0), WHEEL_DELTA(120);

		private final int value;

		DwData(int enumValue) {
			value = enumValue;
		}

		public int getValue() {
			return value;
		}
	}
}

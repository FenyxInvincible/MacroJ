package local.autohotkey.jna.hook.key;

import com.sun.jna.platform.win32.WinUser;

import local.autohotkey.jna.hook.DeviceHookThread;

class KeyHookThread extends DeviceHookThread<KeyEventReceiver> {
	public KeyHookThread(KeyEventReceiver eventReceiver) {
		super(eventReceiver, WinUser.WH_KEYBOARD_LL);
	}

	@Override
	public void onFail() {
		System.err.println("Invalid message result for keyboard hook, aborting");
	}
}

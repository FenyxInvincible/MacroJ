package local.autohotkey.jna.hook.key;

import local.autohotkey.jna.hook.DeviceHookManager;

/**
 * A registrar for keeping track of keyboard event receivers.
 * 
 * @author Matt
 */
public class KeyHookManager extends DeviceHookManager<KeyEventReceiver, KeyHookThread> {
	@Override
	public KeyHookThread createHookThread(KeyEventReceiver eventReceiver) {
		return new KeyHookThread(eventReceiver);
	}
}
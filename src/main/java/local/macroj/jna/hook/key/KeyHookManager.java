package local.macroj.jna.hook.key;

import local.macroj.jna.hook.DeviceHookManager;

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
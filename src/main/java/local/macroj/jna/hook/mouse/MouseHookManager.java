package local.macroj.jna.hook.mouse;

import local.macroj.jna.hook.DeviceHookManager;

/**
 * A registrar for keeping track of mouse event receivers.
 * 
 * @author Matt
 */
public class MouseHookManager extends DeviceHookManager<MouseEventReceiver, MouseHookThread> {
	@Override
	public MouseHookThread createHookThread(MouseEventReceiver eventReceiver) {
		return new MouseHookThread(eventReceiver);
	}
}
package local.macroj.sender;

import local.macroj.data.Key;
import local.macroj.key.MouseKey;

/**
 * If implementation can send dwExtraInfo, then default implementation should prevent recursive calls macro by sending
 * dwExtraInfo = new ULONG_PTR(IS_MACRO);
 * For example macro is bind to key "1" and it send pressKey(1) inside. If recursive is allowed, macro will call itself for each iteration.
 * To prevent it MacroListener has check dwExtraInfo == IS_MACRO and default implementations SHOULD try to prevent recursive calls.
 * Some implementations may not have possibility to send dwExtraInfo as Java Robot.
 *
 * Default implementations of methods should prevent recursive calls
 */
public interface Sender {
    /**
     *
     * @param key
     */
    default void pressKey(Key key) {
        pressKey(key, false);
    };

    /**
     *
     * @param key
     * @param allowRecursive see class description
     */
    void pressKey(Key key, boolean allowRecursive);

    /**
     *
     * @param key
     */
    default void releaseKey(Key key){
        releaseKey(key, false);
    };

    /**
     *
     * @param key
     * @param allowRecursive see class description
     */
    void releaseKey(Key key, boolean allowRecursive);

    /**
     *
     * @param key
     * @param delay
     */
    default void sendKey(Key key, int delay){
        sendKey(key, delay, false);
    };

    /**
     *
     * @param key
     * @param delay
     * @param allowRecursive  see class description
     */
    void sendKey(Key key, int delay, boolean allowRecursive);

    /**
     *
     * @param key
     */
    default void mouseKeyClick(MouseKey key) {
        mouseKeyClick(key, false);
    };

    /**
     *
     * @param key
     * @param allowRecursive  see class description
     */
    void mouseKeyClick(MouseKey key, boolean allowRecursive);

    /**
     *
     * @param key
     */
    default void mouseKeyPress(MouseKey key){
        mouseKeyPress(key, false);
    };

    /**
     *
     * @param key
     * @param allowRecursive see class description
     */
    void mouseKeyPress(MouseKey key, boolean allowRecursive);

    /**
     *
     * @param key
     */
    default void mouseKeyRelease(MouseKey key){
        mouseKeyRelease(key, false);
    };

    /**
     *
     * @param key
     * @param allowRecursive see class description
     */
    void mouseKeyRelease(MouseKey key, boolean allowRecursive);

    /**
     *
     * @param key
     * @param delay
     * @throws InterruptedException
     */
    default void sendMouseKey(MouseKey key, int delay) throws InterruptedException {
        sendMouseKey(key, delay, false);
    };

    /**
     *
     * @param key
     * @param delay
     * @param allowRecursive see class description
     * @throws InterruptedException
     */
    void sendMouseKey(MouseKey key, int delay, boolean allowRecursive) throws InterruptedException;

    /**
     *
     * @param x
     * @param y
     */
    void mouseMove(int x, int y);
}

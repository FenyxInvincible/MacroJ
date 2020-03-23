package local.autohotkey.key;

import local.autohotkey.data.Key;

public interface Sender {
    void pressKey(Key key);
    void releaseKey(Key key);
    void sendKey(Key key, int delay);
    void mouseLeftClick();
}

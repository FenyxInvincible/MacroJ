package local.autohotkey.sender;

import local.autohotkey.data.Key;
import local.autohotkey.key.MouseKey;

public interface Sender {
    void pressKey(Key key);
    void releaseKey(Key key);
    void sendKey(Key key, int delay);
    void mouseKeyClick(MouseKey key);
    void mouseKeyPress(MouseKey key);
    void mouseKeyRelease(MouseKey key);
    void mouseMove(int x, int y);
}

package local.autohotkey.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class Overlay extends Thread {
    private final String overlayWindowId;
    private OverlayCanvas overlayWindow;

    public Overlay() {
        overlayWindowId = "Overlay" + UUID.randomUUID();
    }

    @PostConstruct
    public void init() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        overlayWindow = new OverlayCanvas(overlayWindowId);
        overlayWindow.setUndecorated(true);
        overlayWindow.setBackground(new Color(0, 0, 0, 0));
        overlayWindow.setAlwaysOnTop(true);
        overlayWindow.setSize(screenSize.width, screenSize.height);
        overlayWindow.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        overlayWindow.getContentPane().setLayout(new java.awt.BorderLayout());
        start();
    }

    @Override
    public void run() {
        overlayWindow.setVisible(true);

        WinDef.HWND hwnd = User32.INSTANCE.FindWindow("SunAwtFrame", overlayWindowId);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        wl = wl | 0x80000 | 0x20;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }

    public void draw(String drawId, Consumer<Graphics> graphicsFunction) {
        overlayWindow.getGraphicsFunctions().put(drawId, graphicsFunction);
        overlayWindow.repaint();
    }

    public void clear(String drawId){
        overlayWindow.getGraphicsFunctions().remove(drawId);
    }

    public void clean(){
        overlayWindow.getGraphicsFunctions().clear();
    }

    static class OverlayCanvas extends JFrame {

        private Map<String, Consumer<Graphics>> graphicsFunctions = new ConcurrentHashMap<>();

        OverlayCanvas(String overlayWindowId) {
            super(overlayWindowId);
        }

        public Map<String, Consumer<Graphics>> getGraphicsFunctions() {
            return graphicsFunctions;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            graphicsFunctions.entrySet().stream().forEach(
                    e -> e.getValue().accept(g)
            );
        }
    }
}
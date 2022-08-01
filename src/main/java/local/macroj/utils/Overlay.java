package local.macroj.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    public void init() throws IOException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        overlayWindow = new OverlayCanvas(overlayWindowId);
        overlayWindow.setUndecorated(true);
        overlayWindow.setBackground(new Color(0, 0, 0, 0));
        overlayWindow.setAlwaysOnTop(true);
        overlayWindow.setSize(screenSize.width, screenSize.height);
        overlayWindow.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        overlayWindow.getContentPane().setLayout(new java.awt.BorderLayout());
        overlayWindow.setIconImage(ImageIO.read(getClass().getResource("/macroJ.png")));
        overlayWindow.setTitle("MacroJ Overlay");
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

    public static BufferedImage scale(BufferedImage img, int dWidth, int dHeight) {
        Image tmp = img.getScaledInstance(dWidth, dHeight, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(dWidth, dHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
}
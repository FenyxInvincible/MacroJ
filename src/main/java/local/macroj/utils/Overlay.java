package local.macroj.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

@Component
@Slf4j
public class Overlay extends Thread {
    private final String overlayWindowId = "MacroJ Overlay";
    private OverlayCanvas overlayWindow;

    public Overlay() {
    }

    public static OverlayLabel getRandomLabel() {
        return getRandomLabelWithZOrder(0);
    }

    public static OverlayLabel getRandomLabelWithZOrder(int i) {
        return OverlayLabel.of(i);
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

    public void draw(OverlayLabel drawId, Consumer<Graphics> graphicsFunction) {
        log.trace("Add graphic func id {}", drawId);
        overlayWindow.getGraphicsFunctions().put(drawId, graphicsFunction);
        overlayWindow.repaint();
    }

    public void clear(OverlayLabel drawId){
        log.trace("Clear graphic func id {}", drawId);
        overlayWindow.getGraphicsFunctions().remove(drawId);
        overlayWindow.repaint();
    }

    public void clean(){
        overlayWindow.getGraphicsFunctions().clear();
    }

    static class OverlayCanvas extends JFrame {

        private final Map<OverlayLabel, Consumer<Graphics>> graphicsFunctions = new ConcurrentSkipListMap<>(
                Comparator.comparingInt(OverlayLabel::getZOrder)
                        .thenComparingLong(OverlayLabel::getCreated)
                        .thenComparing(OverlayLabel::getUuid)
        );

        OverlayCanvas(String overlayWindowId) {
            super(overlayWindowId);
        }

        public Map<OverlayLabel, Consumer<Graphics>> getGraphicsFunctions() {
            return graphicsFunctions;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            log.trace("Draw repaint");
            graphicsFunctions.entrySet().forEach(

                    e -> {
                        log.trace("Repaint item {}", e.getKey());
                        e.getValue().accept(g);
                    }
            );
        }
    }

    public static BufferedImage scale(BufferedImage img, int dWidth, int dHeight) {
        Image tmp = img.getScaledInstance(dWidth, dHeight, Image.SCALE_SMOOTH);
        BufferedImage bImg = new BufferedImage(dWidth, dHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bImg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return bImg;
    }

    @Value(staticConstructor = "of")
    public static class OverlayLabel {
        //Map will be sorted by zOrder
        int zOrder;
        long created = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
    }
}
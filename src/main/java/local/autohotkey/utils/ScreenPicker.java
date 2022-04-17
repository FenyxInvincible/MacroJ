package local.autohotkey.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
public class ScreenPicker {
    public static final int DWORD_WHITE = 16777215;

    private static Robot robot = null;
    private static final WinDef.HDC hdc = User32.INSTANCE.GetDC(null);

    public static BufferedImage screenshot() throws IOException {
        initRobot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = robot.createScreenCapture(screenRect);
        return capture;
    }

    public static int pickDwordColor(int x, int y) {
        return MyGDI32.INSTANCE.GetPixel(hdc, x, y);
    }

    public static Color pickRGBColor(int x, int y) {
        return dwordToColor(pickDwordColor(x, y));
    }

    public static Color dwordToColor(int dword) {
        return new Color(
                dword & 0xFF,
                (dword >> 8) & 0xFF,
                (dword >> 16) & 0xFF
        );
    }

    public static BufferedImage getImageByCoords(int x, int y, int size) {
        int imgSize = size * 2 + 1;

        Color[][] image = new Color[imgSize][imgSize];
        int imgX = 0;
        int imgY = 0;
        for (int i = x - size; i <= x + size; i++) {
            for (int k = y - size; k <= y + size; k++) {
                image[imgX][imgY] = dwordToColor(pickDwordColor(i, k));
                imgY++;
            }
            imgY = 0;
            imgX++;
        }

        BufferedImage bufferedImage = new BufferedImage(image.length, image[0].length,
                BufferedImage.TYPE_INT_RGB);

        for (int a = 0; a < image.length; a++) {
            for (int b = 0; b < image[a].length; b++) {
                bufferedImage.setRGB(a, b, image[a][b].getRGB());
            }
        }
        return bufferedImage;
    }

    public static boolean colorCloseTo(Color target, Color expected, int delta) {
        return target.getRed() < expected.getRed() + delta &&
                target.getRed() > expected.getRed() - delta &&
                target.getGreen() < expected.getGreen() + delta &&
                target.getGreen() > expected.getGreen() - delta &&
                target.getBlue() < expected.getBlue() + delta &&
                target.getBlue() > expected.getBlue() - delta;
    }

    private static synchronized void initRobot() {
        if (robot == null) {
            try {
                robot = new Robot();
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }

}

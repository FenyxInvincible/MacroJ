package local.autohotkey.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

@Slf4j
public class ScreenPicker {
    public static final int DWORD_WHITE = 16777215;

    private static Robot robot = null;
    private static final WinDef.HDC hdc = User32.INSTANCE.GetDC(null);

    /*public static Color pickColor(int x, int y) {
        initRobot();
        //Toolkit.getDefaultToolkit().sync();
        return robot.getPixelColor(20, 20);
    }*/
    public static int pickDwordColor(int x, int y) {
        return MyGDI32.INSTANCE.GetPixel(hdc, x, y);
    }

    public static Color dwordToColor(int dword) {
        return new Color(
                (dword >> 16) & 0xFF,
                (dword >> 8) & 0xFF,
                dword & 0xFF
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

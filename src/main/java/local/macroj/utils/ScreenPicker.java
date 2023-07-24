package local.macroj.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.sun.jna.platform.win32.WinNT.PROCESS_QUERY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.PROCESS_VM_READ;

@Slf4j
public class ScreenPicker {
    public static final int DWORD_WHITE = 16777215;

    private static Robot robot = null;
    private static WinDef.HDC hdc = User32.INSTANCE.GetDC(null);

    public static String getForegroundWindowTitle() {
        char[] windowText = new char[256];
        WinDef.HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(foregroundWindow, windowText, windowText.length);
        return Native.toString(windowText);
    }

    public static String getForegroundWindowPath() {
        char[] windowPath = new char[512];

        Psapi psapi = Psapi.INSTANCE;
        IntByReference pointer = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(User32.INSTANCE.GetForegroundWindow(), pointer);
        final Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE process = kernel32.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
        psapi.GetModuleFileNameExW(process, null, windowPath, windowPath.length);
        Kernel32.INSTANCE.CloseHandle(process);

        return Native.toString(windowPath);
    }

    public static BufferedImage screenshot() throws IOException {
        initRobot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = robot.createScreenCapture(screenRect);
        return capture;
    }

    synchronized public static int pickDwordColor(int x, int y) {
        RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handleResult(-1)
                .withMaxRetries(3)
                .onRetry(event -> {
                    User32.INSTANCE.ReleaseDC(null, hdc);
                    hdc = User32.INSTANCE.GetDC(null);
                    log.warn("DC was reset in ScreenPicker pickDwordColor");
                })
                .build();

        return Failsafe.with(retryPolicy).get(() -> MyGDI32.INSTANCE.GetPixel(hdc, x, y));
    }

    public static Color pickRGBColor(int x, int y) {
        return dwordToColor(pickDwordColor(x, y));
    }

    public static Color dwordToColor(int dword) {
        if(dword < 0) {
            throw new IllegalArgumentException("Incorrect dword value " + dword);
        }

        return new Color(
                (dword >> 16) & 0xFF,
                (dword >> 8) & 0xFF,
                dword & 0xFF
        );
    }

    public static BufferedImage captureScreen(int x, int y, int sizeX, int sizeY) {
        return MyGDI32.INSTANCE.captureScreen(hdc, x, y, sizeX, sizeY);
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

    public static File getRootDir(Object obj) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        File rootDir = new File(classLoader.getResource("").getFile());
        return new File(rootDir.getAbsolutePath());
    }

    public static void saveImageAsJpg(BufferedImage img, File file) {
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

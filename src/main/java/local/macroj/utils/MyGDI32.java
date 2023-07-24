package local.macroj.utils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.W32APIOptions;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public interface MyGDI32 extends com.sun.jna.platform.win32.GDI32 {
    MyGDI32 INSTANCE = Native.load("gdi32", MyGDI32.class, W32APIOptions.DEFAULT_OPTIONS);
    int GetPixel(WinDef.HDC hdc, int x, int y);

    public default BufferedImage captureScreen(WinDef.HDC hdc, int x, int y, int width, int height) {
        BufferedImage image = null;
        WinDef.HDC hdcMemDC = MyGDI32.INSTANCE.CreateCompatibleDC(hdc);

        WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdc, width, height);
        WinNT.HANDLE hOld = MyGDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        try {
            MyGDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdc, x, y, GDI32.SRCCOPY);

            WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
            bmi.bmiHeader.biWidth = width;
            bmi.bmiHeader.biHeight = -height;
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

            Memory bufferMemory = new Memory(width * height * 4);
            GDI32.INSTANCE.GetDIBits(hdc, hBitmap, 0, height, bufferMemory, bmi, WinGDI.DIB_RGB_COLORS);

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            for (int i = 0; i < pixels.length; i++) {
                int b = bufferMemory.getByte(i * 4) & 0xFF;
                int g = bufferMemory.getByte(i * 4 + 1) & 0xFF;
                int r = bufferMemory.getByte(i * 4 + 2) & 0xFF;
                int a = bufferMemory.getByte(i * 4 + 3) & 0xFF;

                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        } finally {
            GDI32.INSTANCE.DeleteObject(hBitmap);
            MyGDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
            MyGDI32.INSTANCE.DeleteDC(hdcMemDC);
        }

        return image;
    }
}

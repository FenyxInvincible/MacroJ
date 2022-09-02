package local.macroj.data;

import lombok.Data;

import java.awt.*;

@Data
public class FontData {
    private String name = "TimesRoman";
    private FontStyle style = FontStyle.PLAIN;
    private Integer size = 12;
    private Color color = new Color(0, 0, 0);

    public enum FontStyle {
        PLAIN (0),
        BOLD(1),
        ITALIC(2);

        private final int value;

        FontStyle(int i) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }
}

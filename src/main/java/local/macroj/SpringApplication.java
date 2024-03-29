package local.macroj;

import local.macroj.gui.MainScreen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
@Slf4j
public class SpringApplication {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) {
        try {

            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());

            ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);

            EventQueue.invokeLater(() -> {

                MainScreen ex = context.getBean(MainScreen.class);
                ex.setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "<html><body><p style='width: 200px;'>"+e.getMessage()+"</p></body></html>",
                    "Critical error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }
    }
}
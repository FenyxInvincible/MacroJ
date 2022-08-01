package local.macroj.gui;

import local.macroj.data.RuntimeConfig;
import local.macroj.service.GuiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
@Getter
public class MainScreen extends JFrame {

    @Autowired
    private GuiService guiService;

    @Value("${app.gui.title}")
    private final String windowTitle;

    @Value("${app.gui.width}")
    private final int windowWidth;

    @Value("${app.gui.height}")
    private final int windowHeight;

    private final MainPane mainPane;
    private final Menu mainMenu;

    @PostConstruct
    private void init() throws IOException {

        guiService.registerGui(this);

        setJMenuBar(mainMenu);
        setContentPane(mainPane);

        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(ImageIO.read(getClass().getResource("/macroJ.png")));
    }
}

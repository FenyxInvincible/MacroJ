package local.macroj.service;

import local.macroj.ProfileRunner;
import local.macroj.data.RuntimeConfig;
import local.macroj.gui.MainScreen;
import local.macroj.utils.LambdaTimer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.*;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuiService {

    private final ProfileRunner runner;
    private final RuntimeConfig settings;
    private final MemoryLogging memoryLogging;

    @Value("${app.profilesPath}")
    private final String profilesPath;
    /**
     * Has to be registered
     */
    private MainScreen mainScreen;

    private LambdaTimer timer = new LambdaTimer();
    private TimerTask timerTask;

    public Result addProfile(String name) {
        if(name.isEmpty()) {
            return new Result(false, "Empty profile name");
        }

        String fixedName = name.replaceAll("\\s+","-").
                replaceAll("[^A-Za-z0-9_-]", "");

        File file = new File(MacroFactory.getMacroConfigFilePath(profilesPath, fixedName));

        if (file.exists()) {
            return new Result(false, String.format("Profile %s (%s) already exists", name, fixedName));
        }

        log.debug("Creation new profile: {}", name);

        String template = this.getClass().getResource("/newProfileTemplate.json").getFile();
        try(
            FileWriter writer = new FileWriter(file);
            BufferedReader br = new BufferedReader(new FileReader(template));
        ) {
            writer.write(
                    br.lines().collect(Collectors.joining("\n"))
            );

            mainScreen.getMainPane().refresh();

            log.debug("New profile has been created: {}", name);

            editProfile(fixedName);

            return new Result(true, String.format("Profile %s created", name));
        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }
    }

    public void registerGui(MainScreen screen) {
        this.mainScreen = screen;
    }

    public boolean loadProfile(String toString) {
        settings.setCurrentProfile(toString);
        return runner.run();
    }

    public String getCurrentProfileName() {
        return settings.getCurrentProfile();
    }

    public void editProfile(String profile) {
        try {
            Runtime.getRuntime().exec(
                    "\"" + settings.getSettings().getDefaultEditor() + "\" \"" +
                    MacroFactory.getMacroConfigFilePath(profilesPath, profile) + "\""
            );
        } catch (IOException e) {
            log.error("Unhandled runtime exception. ", e);
        }
    }

    public void disableLogs(JTextArea logsField) {
        memoryLogging.enableMemoryLogging(false);
        memoryLogging.configureLogLevel("local.macroj", LogLevel.OFF);
        if(timerTask != null) {
            timerTask.cancel();
        }
    }

    public void enableLogs(JTextArea logsField) {
        memoryLogging.enableMemoryLogging(true);
        memoryLogging.configureLogLevel("local.macroj", LogLevel.valueOf(settings.getSettings().getLogLevel()));
        timerTask = timer.schedule(() -> {
            logsField.setText(memoryLogging.getLogs());
        }, 100, 500);
    }

    @Data
    @AllArgsConstructor
    public static class Result {
        private boolean isSuccessful;
        private String message;
    }
}

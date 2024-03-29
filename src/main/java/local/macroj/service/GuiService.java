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
import org.springframework.core.io.ClassPathResource;
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
                replaceAll("[^A-Za-z0-9_-]", "") + ".json";

        File file = new File(MacroFactory.getMacroConfigFilePath(profilesPath, fixedName));

        if (file.exists()) {
            return new Result(false, String.format("Profile %s (%s) already exists", name, fixedName));
        }

        log.debug("Creation new profile: {}", name);

        try(
            FileWriter writer = new FileWriter(file);
        ) {
            writer.write("{}");

            refreshProfilesList();

            log.debug("New profile has been created: {}", name);

            editProfile(fixedName);

            return new Result(true, String.format("Profile %s created", name));
        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }
    }

    public void refreshProfilesList() {
        mainScreen.getMainPane().refresh();
    }

    public void registerGui(MainScreen screen) {
        this.mainScreen = screen;
    }

    public boolean toggleProfile(String profileName) {
        if(!profileName.equals(settings.getCurrentProfile())) {
            settings.setCurrentProfile(profileName);
            if(runner.run()){
                return true;
            } else {
                settings.setCurrentProfile(null);
                return false;
            }
        } else {
            return runner.stop();
        }
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

    public void clearLogs() {
        memoryLogging.clearLogs();
    }

    @Data
    @AllArgsConstructor
    public static class Result {
        private boolean isSuccessful;
        private String message;
    }
}

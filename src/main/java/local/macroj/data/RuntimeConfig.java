package local.macroj.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.Type;

@Data
@Component
@Slf4j
public class RuntimeConfig {
    @Value("${app.config.fileName}")
    private final String configIniFileName;

    private final Gson gson;

    private String currentProfile;

    @Getter
    private Settings settings;


    @PostConstruct
    public void init() {
        File configIniFile = new File(configIniFileName);
        if(configIniFile.exists()) {
            try {
                Type collectionType = TypeToken.get(Settings.class)
                    .getType();
                settings = gson.fromJson(
                        new FileReader(configIniFile),
                        collectionType
                );
            } catch (FileNotFoundException e) {
                log.error("Can not read settings file", e);
            }
        } else {
            settings = new Settings();
            saveSettings();
        }
    }

    public void saveSettings() {
        File configIniFile = new File(configIniFileName);
        try(FileWriter file = new FileWriter(configIniFile)) {
            file.write(gson.toJson(settings));
        } catch (IOException e) {
            log.error("Can not write to settings file", e);
        }
    }

    @lombok.Data
    public static final class Settings {
        private String defaultEditor = "Notepad";
        private String logLevel = "INFO";
        private int maxLogLines = 50;
    }
}

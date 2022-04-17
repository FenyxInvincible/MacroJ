package local.autohotkey;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
public class Application {
    public static String PROFILE;

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) {
        try {
            selectProfile();

            ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
            ApplicationRunner bean = context.getBean(ApplicationRunner.class);
            bean.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void selectProfile() {

        String[] profilesArray = listProfilesForFolder(new File("./profiles")).toArray(new String[0]);
        JComboBox profiles = new JComboBox(profilesArray);
        profiles.setEditable(true);

        int choice = JOptionPane.showConfirmDialog(null, profiles, "Select profile", JOptionPane.OK_CANCEL_OPTION);

        if(choice != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        log.info("Selected profile: {}", profiles.getSelectedItem());
        PROFILE = profiles.getSelectedItem().toString();
    }

    private static List<String> listProfilesForFolder(final File folder) {

        return Arrays.stream(folder.listFiles()).sorted().
                filter(f -> !f.isDirectory() && f.getName().startsWith("mapping-"))
                .map(f -> f.getName().replace("mapping-", "")
                        .replace(".json", ""))
                .collect(Collectors.toList());
    }
}
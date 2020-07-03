package local.autohotkey;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import javax.swing.*;

@SpringBootApplication
@Slf4j
public class Application {
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

    private static String selectProfile() {

        String[] profilesArray = {"default", "eso", "mnb", "fallout"};
        JComboBox profiles = new JComboBox(profilesArray);
        profiles.setEditable(true);

        JOptionPane.showMessageDialog(null, profiles, "select or type a value", JOptionPane.QUESTION_MESSAGE);
        log.info("Selected profile: {}", profiles.getSelectedItem());
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profiles.getSelectedItem().toString());
        return "";
    }
}
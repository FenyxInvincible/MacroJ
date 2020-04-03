package local.autohotkey;

import local.autohotkey.key.JnaSender;
import local.autohotkey.key.RobotSender;
import local.autohotkey.key.Sender;
import me.coley.simplejna.hook.key.KeyHookManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.awt.*;

@Configuration
@Profile("eso")
public class EsoConfig {
    @Bean
    public Robot robot() throws AWTException {
        return new Robot();
    }
    @Bean
    public KeyHookManager keyHookManager() {
        return new KeyHookManager();
    }

    @Bean
    public Sender sender(){
        return new JnaSender();
    }
}

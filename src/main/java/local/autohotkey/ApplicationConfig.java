package local.autohotkey;

import local.autohotkey.sender.JnaSender;
import local.autohotkey.sender.Sender;
import local.autohotkey.jna.hook.key.KeyHookManager;
import local.autohotkey.jna.hook.mouse.MouseHookManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class ApplicationConfig {
    @Bean
    public Robot robot() throws AWTException {
        return new Robot();
    }
    @Bean
    public KeyHookManager keyHookManager() {
        return new KeyHookManager();
    }

    @Bean
    public MouseHookManager mouseHookManager() {
        return new MouseHookManager();
    }

    @Bean
    public Sender sender(){
        return new JnaSender();
    }
}

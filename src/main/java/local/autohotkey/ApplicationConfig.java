package local.autohotkey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import local.autohotkey.data.Key;
import local.autohotkey.sender.JnaSender;
import local.autohotkey.sender.Sender;
import local.autohotkey.jna.hook.key.KeyHookManager;
import local.autohotkey.jna.hook.mouse.MouseHookManager;
import local.autohotkey.serialization.KeyDeserializer;
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

    @Bean
    public Gson gson(KeyDeserializer keyDeserializer){
        return new GsonBuilder()
                .registerTypeAdapter(Key.class, keyDeserializer)
                .create();
    }
}

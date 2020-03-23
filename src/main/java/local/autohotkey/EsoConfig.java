package local.autohotkey;

import local.autohotkey.key.RobotSender;
import local.autohotkey.key.Sender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.*;

@Configuration
@Profile("eso")
public class EsoConfig {
    @Bean
    public Robot robot() throws AWTException {
        return new Robot();
    }

    @Bean
    public Sender sender(Robot robot){
        return new RobotSender(robot);
    }
}

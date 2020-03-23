package local.autohotkey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class Application {
    static {
        System.setProperty("java.awt.headless", "false");
    }
    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
            ApplicationRunner bean = context.getBean(ApplicationRunner.class);
            bean.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
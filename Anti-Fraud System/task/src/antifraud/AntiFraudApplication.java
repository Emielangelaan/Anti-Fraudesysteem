package antifraud;

import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AntiFraudApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AntiFraudApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    public AntiFraudApplication() {
        LoggerFactory.getLogger(AntiFraudApplication.class).warn("started");
    }
}
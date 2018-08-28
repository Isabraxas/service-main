package cc.viridian.service.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.ZoneId;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MainApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MainApplication.class, args);

        ZoneId defaultZoneId = ZoneId.systemDefault();
        log.info("System Default TimeZone : " + defaultZoneId);

        //sandbox
        /*
        */
    }
}

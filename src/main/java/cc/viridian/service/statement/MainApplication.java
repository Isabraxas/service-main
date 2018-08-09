package cc.viridian.service.statement;

import cc.viridian.provider.model.Statement;
import cc.viridian.service.statement.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.ZoneId;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);

        ZoneId defaultZoneId = ZoneId.systemDefault();
        log.info("System Default TimeZone : " + defaultZoneId);

		//sandbox
        /*
        Statement statement = ScheduleService.getInstance().getRandomStatement();

        System.out.println( statement);
        System.out.println( statement.getHeader());
        System.out.println( statement.getDetails());
        */
    }

}

package cc.viridian.service.statement;

import cc.viridian.provider.model.Statement;
import cc.viridian.service.statement.service.ScheduleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    @Scheduled(cron = "0 * * * * ?")  //each minute at 0 seconds
    //@Scheduled(fixedDelay = 1000)
    public void scheduleTaskUsingCronExpression() {

        long now = System.currentTimeMillis() / 1000;
        System.out.println("Current Thread : " +  Thread.currentThread().getName());

        //Statement statement = ScheduleService.getInstance().getRandomStatement();

        //System.out.println( statement);
        //System.out.println( statement.getHeader());
        //System.out.println( statement.getDetails());

    }
}

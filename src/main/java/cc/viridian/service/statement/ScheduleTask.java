package cc.viridian.service.statement;

import cc.viridian.provider.exception.CorebankException;
import cc.viridian.service.statement.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    private ScheduleService scheduleService;

    @Autowired
    public ScheduleTask(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Scheduled(cron = "0 * * * * ?")  //each minute at 0 seconds
    public void scheduleTaskUsingCronExpression() {

        try {
            if (scheduleService.isThreadIdle()) {
                scheduleService.retryJobs();
                scheduleService.retrySender();
            }

        } catch (CorebankException e) {
            log.error(e.getMessage());
        }
    }
}

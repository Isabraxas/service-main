package cc.viridian.service.statement.config;

import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.service.RetrySenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

public class CheckbeanConfigSender implements CommandLineRunner {

    @Autowired
    private RetrySenderService retrySenderService;


    public static void main(String[] args) {
        SpringApplication.run(CheckbeanConfigSender.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String topic = "dev-sender2";
        Integer partition = 0;
        Integer offset = 100;

        SenderTemplate senderTemplate = retrySenderService.getSendersTemplateByOffset(topic, partition, offset);

        System.out.println(senderTemplate);
    }
}

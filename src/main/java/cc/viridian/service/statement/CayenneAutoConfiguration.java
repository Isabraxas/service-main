package cc.viridian.service.statement;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootConfiguration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class CayenneAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(CayenneAutoConfiguration.class);

    @Configuration
    protected static class CayenneClientAutoConfiguration {

        @Value("${database.jdbc-driver}")
        private String jdbcDriver;

        @Value("${database.url}")
        private String url;

        @Value("${database.user}")
        private String user;

        @Value("${database.password}")
        private String password;

        @Bean
        public ServerRuntime mainServerRuntime() {

            //url = "jdbc:postgresql://localhost:5432/demo";
            url = "jdbc:postgresql://10.1.20.15:5432/statement";
            ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("persistence/cayenne-statement.xml")
                .jdbcDriver( jdbcDriver)
                .url(url)
                .user(user)
                .password(password)
                .build();

            log.info("connecting to database: " + url);
            return serverRuntime;
        }

    }
}
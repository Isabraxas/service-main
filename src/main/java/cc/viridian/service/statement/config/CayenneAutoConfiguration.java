package cc.viridian.service.statement.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@SpringBootConfiguration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class CayenneAutoConfiguration {

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

            ServerRuntime serverRuntime = ServerRuntime.builder()
                                                       .addConfig("persistence/cayenne-statement.xml")
                                                       .jdbcDriver(jdbcDriver)
                                                       .url(url)
                                                       .user(user)
                                                       .password(password)
                                                       .build();

            log.info("connecting to database: " + url);
            return serverRuntime;
        }
    }
}

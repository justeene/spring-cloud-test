package com.xzg.test;

import com.xzg.test.config.EsChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午3:50
 */
@EnableBinding(EsChannel.class)
//exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
@SpringBootApplication
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        LOGGER.info("app started");
    }
}
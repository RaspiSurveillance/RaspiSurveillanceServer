package de.calltopower.raspisurveillance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Main application class
 */
@Configuration
@EnableConfigurationProperties
@SpringBootApplication
// @formatter:off
@PropertySources(value = {
    @PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
})
@EnableAutoConfiguration/*(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})*/
//@formatter:on
public class RSApplication {

    /**
     * Bootstraps the spring application
     * 
     * @param args The arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RSApplication.class);
    }

}

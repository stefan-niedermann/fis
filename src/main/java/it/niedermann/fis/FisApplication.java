package it.niedermann.fis;

import org.apache.catalina.filters.RemoteAddrFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FisConfiguration.class)
public class FisApplication {

    private static final Logger logger = LoggerFactory.getLogger(FisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FisApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<RemoteAddrFilter> remoteAddressFilter(
            FisConfiguration configuration
    ) {

        var filterRegistrationBean = new FilterRegistrationBean<RemoteAddrFilter>();
        if (configuration.getSecurity().getAllow().length > 0) {
            var filter = new RemoteAddrFilter();
            filter.setAllow("127.0.0.1");
            for (var ip : configuration.getSecurity().getAllow()) {
                logger.debug("Allow " + ip);
                filter.setAllow(ip);
            }
            filterRegistrationBean.setFilter(filter);
            filterRegistrationBean.addUrlPatterns("/*");
        } else {
            logger.debug("No IP address filters will be applied.");
        }
        return filterRegistrationBean;
    }

}

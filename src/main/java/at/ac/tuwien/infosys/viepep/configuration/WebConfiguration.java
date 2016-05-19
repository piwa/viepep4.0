package at.ac.tuwien.infosys.viepep.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by philippwaibel on 10/03/16.
 */
@Configuration
@EnableWebMvc
public class WebConfiguration {
    /*
    @Bean
    ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
    */
}
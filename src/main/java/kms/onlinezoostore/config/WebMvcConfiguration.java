package kms.onlinezoostore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/*")
                .allowedOrigins("*")
                .allowCredentials(false)
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowedMethods("*")
                .maxAge(60*30)
                ;

    }
}

package kms.onlinezoostore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
//                .allowedOrigins("*") // allow access from any source
                .allowedOrigins("https://clydeurov.github.io", "https://vvolk-valeria.github.io", "http://localhost:3000")
                .allowCredentials(true) // allow sending cookies and authentication headers
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600)
                ;
    }
}

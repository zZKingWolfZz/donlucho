package com.example.donlucho.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map standard paths
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");

        // Map legacy folders/prefixes
        registry.addResourceHandler("/Login/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/habitaciones/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/utiles/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/utiles/**").addResourceLocations("classpath:/static/");
        
        // Map direct files to parent directories
        registry.addResourceHandler("/bot.css").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/style.css").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/videofondo.mp4").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/script.js").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/info/style_info.css").addResourceLocations("classpath:/static/css/");
        
        // Match script.js from any subpath depth
        registry.addResourceHandler("/**/script.js").addResourceLocations("classpath:/static/js/");
    }
}

package com.angel.springboot.app.springboot_intellij.config;

import com.angel.springboot.app.springboot_intellij.interceptor.UsuarioInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UsuarioInterceptor usuarioInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(usuarioInterceptor)
                .addPathPatterns("/**") // Aplica a todas las rutas
                .excludePathPatterns("/login", "/logout", "/css/**", "/js/**", "/images/**"); // Excluye rutas públicas
    }
}
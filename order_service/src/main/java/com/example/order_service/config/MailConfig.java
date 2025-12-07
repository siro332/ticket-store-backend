package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost"); // Dummy host
        mailSender.setPort(1025);        // Dummy port
        mailSender.setUsername("test@example.com"); // Dummy username
        mailSender.setPassword("password"); // Dummy password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false"); // No auth needed for dummy
        props.put("mail.smtp.starttls.enable", "false"); // No TLS needed for dummy
        props.put("mail.debug", "false"); // Disable debug logging for mail

        return mailSender;
    }
}

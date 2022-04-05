package com.fondopresente.infraestructura.configuracion;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class ConfiguracionEmail {
	private static final String SPRING_MAIL_HOST = "spring.mail.host";
	private static final String SPRING_MAIL_PORT = "spring.mail.port";
	private static final String SPRING_MAIL_USERNAME = "spring.mail.username";
	private static final String SPRING_MAIL_PASSWORD = "spring.mail.password";

	private static final String HOST = "smtp.gmail.com";
	private static final int PORT = 587;
	private static final String USERNAME = "fpcorreoprueba@gmail.com";
	private static final String PASSWORD = "";

	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender mailSender() {
		String host = env.getProperty(SPRING_MAIL_HOST) == null ? HOST : env.getProperty(SPRING_MAIL_HOST);

		int port = env.getProperty(SPRING_MAIL_PORT) == null ? PORT
				: Integer.valueOf(env.getProperty(SPRING_MAIL_PORT));

		String username = env.getProperty(SPRING_MAIL_USERNAME) == null ? USERNAME
				: env.getProperty(SPRING_MAIL_USERNAME);

		String password = env.getProperty(SPRING_MAIL_PASSWORD) == null ? PASSWORD
				: env.getProperty(SPRING_MAIL_PASSWORD);

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);

		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.from", username);
		props.put("mail.debug", "false");
		return mailSender;
	}

}

package com.joseprecio.projectefinalcurs;

import java.io.File;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.ResourceBundle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.joseprecio.projectefinalcurs.bot.Bot;

@SpringBootApplication
@EnableAutoConfiguration
public class ProjectefinalcursApplication extends WebMvcConfigurerAdapter {

	/**
	 * Resuelve la ubicación de los textos de la web
	 * 
	 * @return 
	 */
	@Bean
	public MessageSource messageSource() {
	    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasenames("classpath:/messages");
	    messageSource.setUseCodeAsDefaultMessage(true);
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	/**
	 * Resuelve el lenguaje
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	/**
	 * Captura de la url el lenguaje en que queremos visualizar la web
	 * 
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	/**
	 * Añade un interceptor para obtener el lenguaje en que se quiere consultar la página
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	/**
	 * Creamos el Bot de la aplicación
	 * 
	 * @return Bot
	 */
	@Bean
	public Bot getBot() {
		return new Bot();
	}

	public static void main(String[] args) {
		ResourceBundle loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME,
				Locale.getDefault(Category.DISPLAY));

		// Iniciamos la aplicación spring
		ApplicationContext context = SpringApplication.run(ProjectefinalcursApplication.class, args);

		// Obtenemos el bot del contexto
		Bot bot = context.getBean(Bot.class);

		try {
			System.out.println("Ruta: " + new File(".").getAbsolutePath());
			
			// Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ loggerResources.getString("msg_init_bot_intentTraining_load"));
			bot.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ loggerResources.getString("msg_end_bot_intentTraining_load"));
		} catch (Exception e) {
			// Mostramos la traza de error
			e.printStackTrace();

			// Cerramos la aplicación
			int exitValue = SpringApplication.exit(context);
			System.exit(exitValue);
		}
	}
}

package com.deliverabilityninja.smtp.smtpadmin.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.properties.EncryptableProperties;
import org.jasypt.spring4.properties.EncryptablePropertyPlaceholderConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.deliverabilityninja.smtp" })
public class RootConfig implements WebMvcConfigurer, ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    static EncryptablePropertyPlaceholderConfigurer propertyConfigurer() {
        EncryptablePropertyPlaceholderConfigurer propertyConfigurer = new EncryptablePropertyPlaceholderConfigurer(
                stringEncryptor());
        propertyConfigurer.setLocations(new ClassPathResource("application.properties"),
                new ClassPathResource("smtpadmin.properties"));
        return propertyConfigurer;
    }

    /**
     * Data source.
     *
     * @return the hikari data source
     * @throws IOException 
     */
    @Bean
    public HikariDataSource dataSource() throws IOException {
        // HikariConfig config = new HikariConfig("/hikari.properties");
        Properties props = new EncryptableProperties(stringEncryptor());
        props.load(new ClassPathResource("hikari.properties").getInputStream());
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("dataSource.url"));
        config.setUsername(props.getProperty("dataSource.user"));
        config.setPassword(props.getProperty("dataSource.password"));
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

    /**
     * Jdbc template.
     *
     * @return the named parameter jdbc template
     * @throws IOException 
     */
    @Bean
    public NamedParameterJdbcTemplate namedJdbcTemplate() throws IOException {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource());
        return jdbcTemplate;
    }

    @Bean("jasyptStringEncryptor")
    public static StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String encryptionKey = System.getenv().get("APP_ENCRYPTION_KEY");
        config.setPassword(encryptionKey);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/resources/**").addResourceLocations("file:/usr/local/pages/static/",
                "classpath:/static/");
    }

    /**
     * Template resolver.
     *
     * @return the spring resource template resolver
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setOrder(1);
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix("/usr/local/pages/templates/mailbridge");
        templateResolver.setSuffix(".html");
        templateResolver.setOrder(2);
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * Template engine.
     *
     * @return the spring template engine
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        Set<ITemplateResolver> templateResolvers = new HashSet<ITemplateResolver>();
        templateResolvers.add(templateResolver());
        templateResolvers.add(fileTemplateResolver());
        // templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setTemplateResolvers(templateResolvers);
        templateEngine.setEnableSpringELCompiler(true);
        // templateEngine.addDialect(securityDialect());
        return templateEngine;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

package com.deliverabilityninja.smtp.smtpadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.deliverabilityninja.smtp.smtpadmin.services.MailcowDomainHandler;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailserverDAO;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailDomainDAO;
import com.deliverabilityninja.smtp.smtpadmin.services.MailcowMailboxHandler;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.SMTP2GOHander;
import com.deliverabilityninja.smtp.smtpadmin.dao.DBUtil;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailboxDAO;
import com.deliverabilityninja.smtp.smtpadmin.dao.ClientDAO;
import com.deliverabilityninja.smtp.smtpadmin.services.GodaddyHandler;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.MailgunHandler;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.SendgridHandler;
import com.deliverabilityninja.smtp.smtpadmin.dao.IpPoolDAO;
import com.deliverabilityninja.smtp.smtpadmin.config.security.SecurityConfig;
import com.deliverabilityninja.smtp.smtpadmin.dao.UsersDAO;


@Configuration
public class AppBeansConfig {

    @Bean
    public MailcowDomainHandler mailcowDomainHandler() {
        return new MailcowDomainHandler();
    }

    @Bean
    public MailserverDAO mailserverDAO() {
        return new MailserverDAO();
    }

    @Bean
    public MailDomainDAO mailDomainDAO() {
        return new MailDomainDAO();
    }

    @Bean
    public MailcowMailboxHandler mailcowMailboxHandler() {
        return new MailcowMailboxHandler();
    }

    @Bean
    public MailboxDAO mailboxDAO() {
        return new MailboxDAO();
    }

    @Bean
    public DBUtil dbUtil() {
        return new DBUtil();
    }

    @Bean
    public ClientDAO clientDAO() {
        return new ClientDAO();
    }

    @Bean
    public SMTP2GOHander smtp2goHandler() {
        return new SMTP2GOHander();
    }

    @Bean
    public GodaddyHandler godaddyHandler() {
        return new GodaddyHandler();
    }

    @Bean
    public MailgunHandler mailgunHandler() {
        return new MailgunHandler();
    }

    @Bean
    public SendgridHandler sendgridHandler() {
        return new SendgridHandler();
    }

    @Bean
    public IpPoolDAO ipPoolDAO() {
        return new IpPoolDAO();
    }

    @Bean
    public SecurityConfig securityConfig() {
        return new SecurityConfig();
    }

    @Bean
    public UsersDAO usersDAO() {
        return new UsersDAO();
    }
}

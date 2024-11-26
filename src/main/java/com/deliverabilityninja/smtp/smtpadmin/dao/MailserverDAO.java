package com.deliverabilityninja.smtp.smtpadmin.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.deliverabilityninja.smtp.smtpadmin.model.Mailserver;
import com.deliverabilityninja.smtp.smtpadmin.model.SMTPProvider;

public class MailserverDAO {

    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Mailserver> getAllMailServers() {
        List<Mailserver> mailservers = new ArrayList<>();
        String query = "SELECT * FROM mailserver";
        mailservers = namedParameterJdbcTemplate.query(query, (resultSet, rowNum) -> {
            Mailserver mailserver = new Mailserver();
            mailserver.setId(resultSet.getLong("id"));
            mailserver.setName(resultSet.getString("name"));
            mailserver.setCnameDomain(resultSet.getString("cname_domain"));
            mailserver.setIpAddress(resultSet.getString("ip_address"));
            mailserver.setStatus(resultSet.getInt("status"));
            mailserver.setSmtpPort(resultSet.getInt("smtp_port"));
            mailserver.setApiKey(resultSet.getString("api_key"));
            return mailserver;
        });

        return mailservers;

    }

    public Mailserver getMailserver(long id) {
        String query = "SELECT * FROM mailserver where id=:id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, rowNum) -> {
            Mailserver mailserver = new Mailserver();
            mailserver.setId(resultSet.getLong("id"));
            mailserver.setName(resultSet.getString("name"));
            mailserver.setCnameDomain(resultSet.getString("cname_domain"));
            mailserver.setIpAddress(resultSet.getString("ip_address"));
            mailserver.setStatus(resultSet.getInt("status"));
            mailserver.setSmtpPort(resultSet.getInt("smtp_port"));
            mailserver.setApiKey(resultSet.getString("api_key"));
            return mailserver;
        });
    }

    public List<SMTPProvider> getSmtpProviders(long domainId) {
        String query = """
                SELECT
                    sp.id,
                    sp.name
                FROM
                    smtpprovider_maildomain_mapping smm
                INNER JOIN smtpprovider sp
                ON sp.id = smm.smtpprovider_id
                WHERE
                    smm.maildomain_id = :domainId
                AND smm.status = 0
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("domainId", domainId);
        List<SMTPProvider> smtpProviders = namedParameterJdbcTemplate.query(query, params, (resultSet, rowNum) -> {
            SMTPProvider smtpProvider = new SMTPProvider();
            smtpProvider.setId(resultSet.getLong("id"));
            smtpProvider.setName(resultSet.getString("name"));
            return smtpProvider;
        });
        return smtpProviders;
    }
}

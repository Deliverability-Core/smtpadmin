package com.deliverabilityninja.smtp.smtpadmin.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.deliverabilityninja.smtp.smtpadmin.dto.MailServerMailDomainDTO;

public class DBUtil {

    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    public MailServerMailDomainDTO getMailServerMailDomain(Long maildomainId) {
        String query = """
        SELECT 
            maildomain.id,
            maildomain.domain_tld,
            mailserver.cname_domain,
            mailserver.api_key
        FROM maildomain 
        JOIN mailserver ON maildomain.mailserver_id = mailserver.id
        WHERE maildomain.id = :maildomain_id;
        """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("maildomain_id", maildomainId);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, rowNum) -> {
            MailServerMailDomainDTO mailServerMailDomainDTO = new MailServerMailDomainDTO();
            mailServerMailDomainDTO.setMaildomainId(resultSet.getLong("id"));
            mailServerMailDomainDTO.setDomainTld(resultSet.getString("domain_tld"));
            mailServerMailDomainDTO.setMailServerCnameDomain(resultSet.getString("cname_domain"));
            mailServerMailDomainDTO.setMailDomainAPIKey(resultSet.getString("api_key"));
            return mailServerMailDomainDTO;
        });
    }
}

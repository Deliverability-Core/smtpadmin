package com.deliverabilityninja.smtp.smtpadmin.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.deliverabilityninja.smtp.smtpadmin.model.Maildomain;
import com.deliverabilityninja.smtp.smtpadmin.dto.DomainListDTO;

public class MailDomainDAO {
    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MailDomainDAO.class);

    public void addMaildomain(Maildomain maildomain) {
        String query = """
                INSERT INTO maildomain (domain_tld, mail_domain, mailserver_id, client_id, status)
                VALUES (:domain_tld, :mail_domain, :mailserver_id, :client_id, :status)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("domain_tld", maildomain.getDomainTld())
                .addValue("mail_domain", maildomain.getMailDomain())
                .addValue("mailserver_id", maildomain.getMailserverId())
                .addValue("client_id", maildomain.getClientId())
                .addValue("status", maildomain.getStatus());
        logger.info("addMaildomain: " + query + " " + params);
        namedParameterJdbcTemplate.update(query, params);
    }

    public List<Maildomain> getMailDomainsByClientId(Long clientId) {
        String query = "SELECT * FROM maildomain WHERE client_id = :clientId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("clientId", clientId);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, rowNum) -> {
            Maildomain maildomain = new Maildomain();
            maildomain.setId(resultSet.getLong("id"));
            maildomain.setDomainTld(resultSet.getString("domain_tld"));
            maildomain.setMailDomain(resultSet.getString("mail_domain"));
            maildomain.setMailserverId(resultSet.getLong("mailserver_id"));
            maildomain.setClientId(resultSet.getLong("client_id"));
            maildomain.setStatus(resultSet.getInt("status"));
            return maildomain;
        });
    }

    public List<Maildomain> getAllDomains() {
        String query = "SELECT * FROM maildomain";
        return namedParameterJdbcTemplate.query(query, (resultSet, rowNum) -> {
            Maildomain maildomain = new Maildomain();
            maildomain.setId(resultSet.getLong("id"));
            maildomain.setDomainTld(resultSet.getString("domain_tld"));
            maildomain.setMailDomain(resultSet.getString("mail_domain"));
            maildomain.setMailserverId(resultSet.getLong("mailserver_id"));
            maildomain.setClientId(resultSet.getLong("client_id"));
            maildomain.setStatus(resultSet.getInt("status"));
            return maildomain;
        });
    }

    public List<DomainListDTO> getAllMailDomains() {
        String query = """
                SELECT
                    c.name,
                    md.domain_tld,
                    md.mail_domain,
                    md.status as domain_status,
                    string_agg(sp.name, ', ') as provider_names
                FROM maildomain md
                LEFT JOIN smtpprovider_maildomain_mapping spmdm
                    ON md.id = spmdm.maildomain_id
                LEFT JOIN smtpprovider sp
                    ON spmdm.smtpprovider_id = sp.id
                INNER JOIN client c
                    ON md.client_id = c.id
                GROUP BY
                    c.name,
                    md.domain_tld,
                    md.mail_domain,
                    md.status
                """;
        return namedParameterJdbcTemplate.query(query, (resultSet, rowNum) -> {
            DomainListDTO domainListDTO = new DomainListDTO();
            domainListDTO.setClientName(resultSet.getString("name"));
            domainListDTO.setDomainTld(resultSet.getString("domain_tld"));
            domainListDTO.setMailDomain(resultSet.getString("mail_domain"));
            domainListDTO.setDomainStatus(resultSet.getInt("domain_status"));
            domainListDTO.setProviderName(resultSet.getString("provider_names"));
            return domainListDTO;
        });
    }

}

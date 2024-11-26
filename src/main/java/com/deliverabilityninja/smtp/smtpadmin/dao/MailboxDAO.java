package com.deliverabilityninja.smtp.smtpadmin.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.deliverabilityninja.smtp.smtpadmin.model.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class MailboxDAO {

    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    public void addMailbox(Mailbox data) {

        String sql = """
            INSERT INTO mailbox (email, password, domain_id, status, created, modified) 
            VALUES (:email, :password, :domain_id, 0, now(), now())
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", data.getEmail())
                .addValue("password", data.getPassword())
                .addValue("domain_id", data.getDomainId());
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Mailbox> getMailboxes(Long domainId) {
        String sql = "SELECT * FROM mailbox";
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (domainId != null) {
            sql += " WHERE domain_id = :domain_id";
            params.addValue("domain_id", domainId);
        }

        List<Mailbox> mailboxes = namedParameterJdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
            Mailbox mailbox = new Mailbox();
            mailbox.setId(resultSet.getLong("id"));
            mailbox.setEmail(resultSet.getString("email"));
            mailbox.setPassword(resultSet.getString("password"));
            mailbox.setDomainId(resultSet.getLong("domain_id"));
            mailbox.setStatus(resultSet.getInt("status"));
            mailbox.setCreated(resultSet.getTimestamp("created"));
            mailbox.setModified(resultSet.getTimestamp("modified"));
            return mailbox;
        });
        return mailboxes;
    }
}

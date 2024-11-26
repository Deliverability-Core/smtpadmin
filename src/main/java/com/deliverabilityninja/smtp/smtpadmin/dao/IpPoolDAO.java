package com.deliverabilityninja.smtp.smtpadmin.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.deliverabilityninja.smtp.smtpadmin.dto.AddIPPoolDTO;
import org.springframework.beans.factory.annotation.Autowired;



public class IpPoolDAO {

    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void addIpPool(AddIPPoolDTO addIPPoolDTO) {
        String sql = """
                INSERT INTO ip_pool (name, domain_id, smtp_id) VALUES (:name, :domainId, :smtpId)
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", addIPPoolDTO.getIpPool())
                .addValue("domainId", addIPPoolDTO.getDomainId())
                .addValue("smtpId", addIPPoolDTO.getSmtpProviderId());
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}

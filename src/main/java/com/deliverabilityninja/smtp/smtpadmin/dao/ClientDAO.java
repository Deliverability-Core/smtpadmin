package com.deliverabilityninja.smtp.smtpadmin.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.deliverabilityninja.smtp.smtpadmin.model.Client;
public class ClientDAO {

    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Client> getClients() {
        String query = "SELECT * FROM client";
        List<Client> clients = new ArrayList<>();
        clients = namedParameterJdbcTemplate.query(query, (resultSet, rowNum) -> {
            Client client = new Client();
            client.setId(resultSet.getLong("id"));
            client.setName(resultSet.getString("name"));
            return client;
        });
        return clients;
    }
}

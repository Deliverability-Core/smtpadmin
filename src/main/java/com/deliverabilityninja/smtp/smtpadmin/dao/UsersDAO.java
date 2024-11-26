package com.deliverabilityninja.smtp.smtpadmin.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.deliverabilityninja.smtp.smtpadmin.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UsersDAO {
    
    @Autowired(required = true)
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Users getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = :username";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        return namedParameterJdbcTemplate.queryForObject(sql, params, (resultSet, rowNum) -> {
            Users user = new Users();
            user.setId(resultSet.getLong("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(resultSet.getString("role"));
            return user;
        });
    }
}

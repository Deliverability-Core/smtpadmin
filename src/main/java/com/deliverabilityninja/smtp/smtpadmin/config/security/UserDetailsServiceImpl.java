package com.deliverabilityninja.smtp.smtpadmin.config.security;

import com.deliverabilityninja.smtp.smtpadmin.dao.UsersDAO;
import com.deliverabilityninja.smtp.smtpadmin.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UsersDAO usersDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: " + username);
        Users user = usersDAO.getUserByUsername(username);
        logger.info("User found: " + user.toString());
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
                .password(user.getPassword())   
                .roles(user.getRole())
                .build();
    }
}
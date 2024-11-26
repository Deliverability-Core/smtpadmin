package com.deliverabilityninja.smtp.smtpadmin.config.security;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringEncryptorPasswordEncoder implements PasswordEncoder {

    private static final Logger logger = LoggerFactory.getLogger(StringEncryptorPasswordEncoder.class);
    
    private StringEncryptor stringEncryptor;

    public void setStringEncryptor(StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    public StringEncryptor getStringEncryptor() {
        return stringEncryptor;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        logger.info("Encoding password: " + rawPassword);
        return stringEncryptor.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return stringEncryptor.decrypt(encodedPassword).equals(rawPassword.toString());
    }
    
}

package com.deliverabilityninja.smtp.smtpadmin.services;

import com.deliverabilityninja.smtp.smtpadmin.dto.MailboxDTO;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailcowDomainCreationPayloadDTO;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailServerMailDomainDTO;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailcowMailboxDTO;
import com.deliverabilityninja.smtp.smtpadmin.model.Mailbox;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailboxDAO;
import com.deliverabilityninja.smtp.smtpadmin.dao.DBUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ajbrown.namemachine.NameGenerator;
import org.ajbrown.namemachine.Name;
import org.passay.CharacterData;
import org.passay.EnglishCharacterData;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;

import org.jasypt.encryption.StringEncryptor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import com.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
public class MailcowMailboxHandler {

    @Autowired
    DBUtil dbUtil;

    @Autowired
    MailboxDAO mailboxDAO;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor encryptor;

    private static final Logger logger = LoggerFactory.getLogger(MailcowMailboxHandler.class);

    public byte[] addMailbox(MailboxDTO data) {
        NameGenerator nameGenerator = new NameGenerator();
        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[] { "First Name", "Last Name", "Email", "Domain", "Password", "Encrypted Password" });
        for (Long domain : data.getDomain()) {
            MailServerMailDomainDTO mailServerDomain = dbUtil.getMailServerMailDomain(domain);
            String domainName = mailServerDomain.getDomainTld();
            for (int i = 0; i < data.getMailboxCount(); i++) {
                Name name = nameGenerator.generateName();
                String firstName = name.getFirstName();
                String lastName = name.getLastName();
                String email = formatEmail(firstName, lastName, domainName);
                while (emailExists(email, mailServerDomain)) {
                    name = nameGenerator.generateName();
                    firstName = name.getFirstName();
                    lastName = name.getLastName();
                    email = formatEmail(firstName, lastName, domainName);
                }
                String password = generatePassword();
                String encryptedPassword = encryptor.encrypt(password);
                if (createMailboxInMailcow(email, encryptedPassword, mailServerDomain)) {
                    Mailbox mailbox = new Mailbox();
                    mailbox.setEmail(email);
                    mailbox.setPassword(encryptedPassword);
                    mailbox.setDomainId(domain);
                    mailboxDAO.addMailbox(mailbox);
                    csvData.add(new String[] { firstName, lastName, email, domainName, password, encryptedPassword });
                    logger.info("Successfully created mailbox {} in mailcow", email);
                } else {
                    logger.error("Failed to create mailbox {} in mailcow", email);
                }
            }
        }
        return generateCSV(csvData);
    }

    private String formatEmail(String firstName, String lastName, String domain) {
        String localPart = firstName.toLowerCase() + "." + lastName.toLowerCase();
        localPart = localPart.replaceAll("[^a-z0-9.]", "");
        return localPart + "@" + domain;
    }

    private boolean emailExists(String email, MailServerMailDomainDTO mailServerDomain) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = "http://localhost/api/v1/get/mailbox/" + email;
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-API-Key", mailServerDomain.getMailDomainAPIKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.warn("API returned error code: {}", response.code());
                return false;
            }

            if (response.body() == null) {
                return false;
            }

            String responseBody = response.body().string();
            JsonNode json = objectMapper.readTree(responseBody);
            logger.debug("Email exists check response: {}", json);

            return !json.isEmpty() && json.get("username") != null;
        } catch (IOException e) {
            logger.error("Error checking email existence: {}", e.getMessage());
            return false;
        }
    }

    private String generatePassword() {
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "INSUFFICIENT_SPECIAL";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+{}[]|;:,.<>?";
            }
        };

        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        CharacterRule digitRule = new CharacterRule(digitChars);
        CharacterRule specialCharRule = new CharacterRule(specialChars);

        lowerCaseRule.setNumberOfCharacters(2);
        upperCaseRule.setNumberOfCharacters(2);
        digitRule.setNumberOfCharacters(2);
        specialCharRule.setNumberOfCharacters(2);

        CharacterRule excludeLookAlikeRule = new CharacterRule(new CharacterData() {
            public String getErrorCode() {
                return "CONTAINS_LOOK_ALIKE";
            }

            public String getCharacters() {
                return "Il1O0";
            }
        });
        PasswordGenerator generator = new PasswordGenerator();

        String password = generator.generatePassword(12, lowerCaseRule, upperCaseRule, digitRule, specialCharRule,
                excludeLookAlikeRule);

        return password;
    }

    private boolean createMailboxInMailcow(String email, String password, MailServerMailDomainDTO mailServerDomain) {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "http://localhost/api/v1/add/mailbox";
        MailcowMailboxDTO requestBody = new MailcowMailboxDTO();
        requestBody.setLocal_part(email.split("@")[0]);
        requestBody.setDomain(email.split("@")[1]);
        requestBody.setPassword(password);
        requestBody.setPassword2(password);
        try {
            String jsonPayload = convertDTOToJson(requestBody);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("X-API-Key", mailServerDomain.getMailDomainAPIKey())
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Error creating mailbox in mailcow: {}", e.getMessage());
        }
        return false;
    }

    private byte[] generateCSV(List<String[]> data) {
        try {
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            try (CSVWriter writer = new CSVWriter(stringWriter)) {
                writer.writeAll(data);
            }
            return stringWriter.toString().getBytes();
        } catch (IOException e) {
            logger.error("Error generating CSV: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }

    private String convertDTOToJson(MailcowMailboxDTO payload) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(payload);
    }
}

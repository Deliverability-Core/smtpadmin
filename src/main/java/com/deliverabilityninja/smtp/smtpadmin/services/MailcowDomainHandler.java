package com.deliverabilityninja.smtp.smtpadmin.services;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deliverabilityninja.smtp.smtpadmin.dao.MailserverDAO;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailDomainDAO;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailcowDomainCreationPayloadDTO;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailcowDomainDTO;
import com.deliverabilityninja.smtp.smtpadmin.model.Maildomain;
import com.deliverabilityninja.smtp.smtpadmin.model.Mailserver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

public class MailcowDomainHandler {

    @Autowired
    private MailserverDAO mailserverDAO;

    @Autowired
    private MailDomainDAO mailDomainDAO;

    private final Logger logger = LoggerFactory.getLogger(MailcowDomainHandler.class);

    public String addDomain(MailcowDomainDTO data) {
        try {
            Mailserver mailserver = mailserverDAO.getMailserver(data.getServer_id());
            OkHttpClient client = new OkHttpClient();
            MailcowDomainCreationPayloadDTO payload = new MailcowDomainCreationPayloadDTO();
            payload.setDomain(data.getDomain());
            String jsonPayload = convertDTOToJson(payload);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            //String apiUrl = "http://localhost/api/v1/add/domain";
            String apiUrl = "http://" + mailserver.getCnameDomain() + "/api/v1/add/domain";
            logger.info("API URL: " + apiUrl);
            logger.info("API Key: " + mailserver.getApiKey());
            logger.info("Payload: " + body.toString());
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("X-API-Key", mailserver.getApiKey())
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to create domain {}: {} ", data.getDomain(), response.body().string());
                    return "ErrorMessage";
                }
                logger.info("Successfully created domain: {} on mailserver {} for client {}", data.getDomain(), mailserver.getCnameDomain(), data.getClient_id());
                Maildomain maildomain = new Maildomain();
                maildomain.setDomainTld(data.getDomain());
                maildomain.setMailDomain("mail." + data.getDomain());
                maildomain.setMailserverId(data.getServer_id());
                maildomain.setStatus(Maildomain.Status.ACTIVE.ordinal());
                maildomain.setClientId(data.getClient_id());
                mailDomainDAO.addMaildomain(maildomain);
                return "SuccessMessage";
            }
        } catch (IOException e) {
            logger.error("Error creating domain {}: {}", data.getDomain(), e.getMessage());
            return "ErrorMessage";
        }
    }

    private String convertDTOToJson(MailcowDomainCreationPayloadDTO payload) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(payload);
    }
}

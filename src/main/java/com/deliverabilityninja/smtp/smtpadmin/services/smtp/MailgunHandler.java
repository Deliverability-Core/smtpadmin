package com.deliverabilityninja.smtp.smtpadmin.services.smtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.DnsRecord;
import com.deliverabilityninja.smtp.smtpadmin.services.GodaddyHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.MediaType;       
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
public class MailgunHandler {

    private static final Logger logger = LoggerFactory.getLogger(MailgunHandler.class); 
    
    @Value("${MAILGUN_USERNAME}")
    private String MAILGUN_USERNAME;
    @Value("${MAILGUN_API_KEY}")
    private String MAILGUN_API_KEY;

    @Autowired
    private GodaddyHandler godaddyHandler;

    public void addDomainInMailgun(String domain) throws IOException {
        logger.info("Adding domain: " + domain);
        // domain = "mail." + domain;
        configureMailgun(domain);
    }

    private void configureMailgun(String domain) throws IOException, JsonProcessingException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "mail." + domain)
                .build();

        // Create request with Basic Auth
        String credentials = Base64.getEncoder().encodeToString((MAILGUN_USERNAME + ":" + MAILGUN_API_KEY).getBytes());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.mailgun.net/v4/domains")
                .addHeader("Authorization", "Basic " + credentials)
                .post(requestBody)
                .build();

        // Execute request
        try (okhttp3.Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Mailgun API error: " + responseBody);
            }

            // Parse response and create DNS records
            parseMailgunResponse(domain, responseBody);
        }
    }

    private void parseMailgunResponse(String domain, String responseBody) throws JsonProcessingException, IOException {
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        List<DnsRecord> dnsRecords = new ArrayList<>();

        // Parse DNS records from the response
        JsonArray records = jsonResponse.getAsJsonArray("dns_records");
        if (records != null) {
            for (JsonElement element : records) {
                String name = element.getAsJsonObject().get("name").getAsString();
                if(name.endsWith("." + domain)) {
                    name = name.substring(0, name.length() - ("." + domain).length());
                }
                JsonObject record = element.getAsJsonObject();
                String recordType = record.get("record_type").getAsString();
                String value = record.get("value").getAsString();
                logger.info("recordType: " + recordType + " name: " + name + " value: " + value);
                dnsRecords.add(new DnsRecord(
                    recordType,
                    name,
                    value,
                    3600
                ));
            }
        }

        // Parse sending DNS records
        JsonArray sendingRecords = jsonResponse.getAsJsonArray("sending_dns_records");
        if (sendingRecords != null) {
            for (JsonElement element : sendingRecords) {
                JsonObject record = element.getAsJsonObject();
                String name = record.get("name").getAsString();
                if(name.endsWith("." + domain)) {
                    name = name.substring(0, name.length() - ("." + domain).length());
                }
                logger.info("recordType: " + record.get("record_type").getAsString() + " name: " + name + " value: " + record.get("value").getAsString());
                dnsRecords.add(new DnsRecord(
                    record.get("record_type").getAsString(),
                    name,
                    record.get("value").getAsString(),
                    3600
                ));
            }
        }

        Map<String, List<DnsRecord>> godaddyRecords = Map.of("mailgun_" + domain, dnsRecords);
        logger.info("godaddyRecords: " + godaddyRecords.toString());
        godaddyHandler.convertToGoDaddyRecords(godaddyRecords, domain);
    }

    public void verifyMailgunDomain(String domain) throws IOException {
        logger.info("Verifying Mailgun domain...");

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.mailgun.net/v4/domains/mail." + domain + "/verify")
                .addHeader("Authorization", "Basic " + 
                    Base64.getEncoder().encodeToString((MAILGUN_USERNAME + ":" + MAILGUN_API_KEY).getBytes()))
                .put(okhttp3.RequestBody.create("", JSON))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Mailgun verification error: " + response.body().string());
            }
            logger.info("Mailgun domain verified successfully");
        }
    }



}

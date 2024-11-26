package com.deliverabilityninja.smtp.smtpadmin.services.smtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.DnsRecord;
import com.deliverabilityninja.smtp.smtpadmin.services.GodaddyHandler;
import com.google.gson.Gson;
import com.sendgrid.SendGrid;

public class SendgridHandler {

    @Autowired
    private GodaddyHandler godaddyHandler;

    private static final Logger logger = LoggerFactory.getLogger(SendgridHandler.class);
    @Value("${SENDGRID_API_KEY}")
    private String SENDGRID_API_KEY;

    public void addDomainInSendgrid(String domain) throws IOException {
        logger.info("Adding domain: " + domain);
        configureSendGrid(domain);
    }

    public void configureSendGrid(String domain) throws IOException {
        logger.info("Configuring SendGrid for domain: " + domain);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        com.sendgrid.Request request = new com.sendgrid.Request();
        request.setMethod(com.sendgrid.Method.POST);
        request.setEndpoint("whitelabel/domains");
        request.addHeader("Authorization", "Bearer " + SENDGRID_API_KEY);

        Map<String, Object> requestBody = Map.of("domain", domain);
        Gson gson = new Gson();
        request.setBody(gson.toJson(requestBody));

        com.sendgrid.Response response = sg.api(request);
        if (response.getStatusCode() != 201) {
            throw new IOException("SendGrid API error: " + response.getBody());
        }

        JsonObject domainData = JsonParser.parseString(response.getBody()).getAsJsonObject();
        parseSendGridResponse(domain, domainData);
    }

    private void parseSendGridResponse(String domain, JsonObject domainData) throws IOException {
        JsonObject dns = domainData.getAsJsonObject("dns");

        List<DnsRecord> recordsList = new ArrayList<>();
        addDnsRecord(dns.getAsJsonObject("mail_cname"), recordsList, domain);
        addDnsRecord(dns.getAsJsonObject("dkim1"), recordsList, domain);
        addDnsRecord(dns.getAsJsonObject("dkim2"), recordsList, domain);
        Map<String, List<DnsRecord>> records = Map.of("sendgrid_" + domain, recordsList);
        logger.info("records: " + records.toString());
        godaddyHandler.convertToGoDaddyRecords(records, domain);
    }

    private static void addDnsRecord(JsonObject record, List<DnsRecord> recordsList, String domain) {
        logger.info("record: " + record.toString());
        String host = record.get("host").getAsString();
        // Remove domain suffix if present
        if (host.endsWith("." + domain)) {
            host = host.substring(0, host.length() - ("." + domain).length());
        }

        if (record != null) {
            recordsList.add(new DnsRecord(record.get("type").getAsString().toUpperCase(), host, record.get("data").getAsString(), 3600));
        }
    }

    public void verifySendGridDomain(String domain) throws IOException {
        String domainId = getDomainId(domain);
        logger.info("Verifying SendGrid domain...");
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        com.sendgrid.Request request = new com.sendgrid.Request();
        request.setMethod(com.sendgrid.Method.POST);
        request.setEndpoint("whitelabel/domains/" + domainId + "/validate");
        request.addHeader("Authorization", "Bearer " + SENDGRID_API_KEY);
        com.sendgrid.Response response = sg.api(request);
        if (response.getStatusCode() != 200) {
            throw new IOException("SendGrid verification error: " + response.getBody());
        }
        logger.info("SendGrid domain verified successfully");
    }

    private String getDomainId(String domain) throws IOException {
        
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        com.sendgrid.Request request = new com.sendgrid.Request();
        request.setMethod(com.sendgrid.Method.GET);
        request.setEndpoint("whitelabel/domains");
        request.addHeader("Authorization", "Bearer " + SENDGRID_API_KEY);

        com.sendgrid.Response response = sg.api(request);
        if (response.getStatusCode() != 200) {
            throw new IOException("SendGrid API error: " + response.getBody());
        }

        // Parse the response array
        JsonObject[] domains = new Gson().fromJson(response.getBody(), JsonObject[].class);
        
        // Find the matching domain
        for (JsonObject domainObj : domains) {
            if (domain.equals(domainObj.get("domain").getAsString())) {
                return domainObj.get("id").getAsString();
            }
        }
        
        throw new IOException("Domain not found in SendGrid: " + domain);
    }

}

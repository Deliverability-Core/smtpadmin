package com.deliverabilityninja.smtp.smtpadmin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.DnsRecord;
import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.GoDaddyDnsRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GodaddyHandler {

    private static final Logger logger = LoggerFactory.getLogger(GodaddyHandler.class);

    @Value("${GODADDY_API_URL}")
    private String GODADDY_API_URL;

    @Value("${GODADDY_API_KEY}")
    private String GODADDY_API_KEY;

    @Value("${GODADDY_API_SECRET}")
    private String GODADDY_API_SECRET;


    public void convertToGoDaddyRecords(Map<String, List<DnsRecord>> records, String domain) throws IOException {
        List<DnsRecord> godaddyRecords = records.values().stream()
                .flatMap(List::stream)
                .<DnsRecord>map(record -> new DnsRecord( 
                        record.getType(),
                        record.getName(),
                        record.getData(),
                        record.getTtl()))
                .collect(Collectors.toList());
        updateGoDaddyDNS(domain, godaddyRecords);
    }

    private void updateGoDaddyDNS(String domain, List<DnsRecord> records) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String url = GODADDY_API_URL + domain + "/records";

        // Get existing NS records
        List<DnsRecord> existingRecords = getExistingGoDaddyRecords(domain);
        List<DnsRecord> nsRecords = existingRecords.stream()
                .filter(record -> record.getType().equalsIgnoreCase("NS"))
                .collect(Collectors.toList());

        // Combine NS records with new records
        List<DnsRecord> allRecords = new ArrayList<>(nsRecords);
        allRecords.addAll(records);

        // Update DNS records
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "sso-key " + GODADDY_API_KEY + ":" + GODADDY_API_SECRET)
                .addHeader("Content-Type", "application/json")
                .patch(RequestBody.create(objectMapper.writeValueAsString(allRecords), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to update GoDaddy DNS records: " + response.body().string());
            }
        }

        logger.info("Successfully updated DNS records in GoDaddy");
    }

    private List<DnsRecord> getExistingGoDaddyRecords(String domain) throws IOException {
        String url = GODADDY_API_URL + domain + "/records";
        logger.info("Getting existing DNS records from GoDaddy for domain: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "sso-key " + GODADDY_API_KEY + ":" + GODADDY_API_SECRET)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to get existing DNS records: " + response.body().string());
                throw new IOException("Failed to get GoDaddy DNS records");
            }
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            GoDaddyDnsRecord[] godaddyRecords = objectMapper.readValue(responseBody, GoDaddyDnsRecord[].class);
            
            return Arrays.stream(godaddyRecords)
                    .map(gr -> new DnsRecord(
                            gr.getType(),
                            gr.getName(),
                            gr.getValue(),
                            gr.getTtl()))
                    .collect(Collectors.toList());
        }
    }
}

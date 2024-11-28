package com.deliverabilityninja.smtp.smtpadmin.services.smtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.deliverabilityninja.smtp.smtpadmin.dto.Smtp2go.SMTP2GOResponse;
import com.deliverabilityninja.smtp.smtpadmin.dto.Smtp2go.SMTP2GOResponse.Domain;
import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.DnsRecord;
import com.deliverabilityninja.smtp.smtpadmin.dto.godaddy.GoDaddyDnsRecord;
import com.deliverabilityninja.smtp.smtpadmin.services.GodaddyHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

public class SMTP2GOHander {

    @Autowired
    private GodaddyHandler godaddyHandler;

    private static final Logger logger = LoggerFactory.getLogger(SMTP2GOHander.class);

    @Value("${GODADDY_API_URL}")
    private String GODADDY_API_URL;
    @Value("${GODADDY_API_KEY}")
    private String GODADDY_API_KEY;
    @Value("${GODADDY_API_SECRET}")
    private String GODADDY_API_SECRET;
    @Value("${SMTP2GO_API_KEY}")
    private String SMTP2GO_API_KEY;

    public void addDomainInSMTP2GO(String domain) throws IOException {
        logger.info("Adding domain: " + domain);
        configureSMTP2GO(domain);
    }

    private void configureSMTP2GO(String domain) throws IOException {
        logger.info("Configuring SMTP2GO for domain: " + domain);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Map<String, Object> requestBody = Map.of(
                "domain", domain,
                "tracking_subdomain", "link",
                "returnpath_subdomain", "return",
                "auto_verify", true,
                "api_key", SMTP2GO_API_KEY);

        Request request = new Request.Builder()
                .url("https://api.smtp2go.com/v3/domain/add")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(requestBody), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("SMTP2GO API error: " + responseBody);
            }
            logger.info("SMTP2GO API response: " + responseBody);
            parseSMTP2GOResponse(domain, responseBody);
        }
    }

    private void parseSMTP2GOResponse(String domain, String responseBody) throws IOException {
        logger.info("Parsing SMTP2GO response");
        ObjectMapper objectMapper = new ObjectMapper();
        SMTP2GOResponse response = objectMapper.readValue(responseBody, SMTP2GOResponse.class);

        Domain domainData = response.getData().getDomains().get(0).getDomain();

        List<DnsRecord> dnsRecords = Arrays.asList(
                new DnsRecord("CNAME",
                        domainData.getDkimSelector() + "._domainkey",
                        domainData.getDkimValue(),
                        3600),
                new DnsRecord("CNAME",
                        domainData.getRpathSelector(),
                        domainData.getRpathValue(),
                        3600),
                new DnsRecord("CNAME",
                        "link",
                        "track.smtp2go.net",
                        3600));

        Map<String, List<DnsRecord>> records = Map.of("smtp2go_" + domain, dnsRecords);
        // List<DnsRecord> godaddyRecords = convertToGoDaddyRecords(records);
        // updateGoDaddyDNS(domain, godaddyRecords);
        godaddyHandler.convertToGoDaddyRecords(records, domain);
    }

    private static List<DnsRecord> convertToGoDaddyRecords(Map<String, List<DnsRecord>> records) {
        return records.values().stream()
                .flatMap(List::stream)
                .<DnsRecord>map(record -> new DnsRecord( 
                        record.getType(),
                        record.getName(),
                        record.getData(),
                        record.getTtl()))
                .collect(Collectors.toList());
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

    public void verifyDomainInSMTP2GO(String domain) throws IOException {
        logger.info("Verifying domain: " + domain);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestBody = Map.of(
                "api_key", SMTP2GO_API_KEY,
                "domain", domain,
                "requisition_ssl", false);

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.smtp2go.com/v3/domain/verify")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(requestBody), JSON))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("SMTP2GO verification error: " + response.body().string());
            }
            logger.info("SMTP2GO domain verified successfully");
        }
    }
}

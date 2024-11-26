package com.deliverabilityninja.smtp.smtpadmin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.deliverabilityninja.smtp.smtpadmin.model.Maildomain;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.SMTP2GOHander;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailDomainDAO;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.MailgunHandler;
import com.deliverabilityninja.smtp.smtpadmin.services.smtp.SendgridHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController("/api")
@PreAuthorize("hasRole('ADMIN')")
public class DomainController {

    @Autowired
    private MailgunHandler mailgunHandler;

    @Autowired
    private SMTP2GOHander smtp2goHandler;

    @Autowired
    private SendgridHandler sendgridHandler;

    private static final Logger logger = LoggerFactory.getLogger(DomainController.class);
    @Autowired
    private MailDomainDAO mailDomainDAO;

    @GetMapping("/domains/{clientId}")
    public List<Maildomain> getDomainsByClientId(@PathVariable Long clientId) {
        logger.info("getDomainsByClientId: " + clientId);
        return mailDomainDAO.getMailDomainsByClientId(clientId);
    }

    @PostMapping("/addDomainInSMTP2GO")
    public ResponseEntity<Map<String, String>> addDomainInSMTP2GO(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("addDomainInSMTP2GO: " + domain);
        try {
            smtp2goHandler.addDomainInSMTP2GO(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully added"));
        } catch (IOException e) {
            logger.error("Error adding domain in SMTP2GO: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/verifyDomainInSMTP2GO")
    public ResponseEntity<Map<String, String>> verifyDomainInSMTP2GO(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("verifyDomainInSMTP2GO: " + domain);
        try {
            smtp2goHandler.verifyDomainInSMTP2GO(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully verified"));
        } catch (IOException e) {
            logger.error("Error verifying domain in SMTP2GO: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/addDomainInMailgun")
    public ResponseEntity<Map<String, String>> addDomainInMailgun(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("addDomainInMailgun: " + domain);
        try {
            mailgunHandler.addDomainInMailgun(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully added"));
        } catch (IOException e) {
            logger.error("Error adding domain in Mailgun: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/verifyDomainInMailgun")
    public ResponseEntity<Map<String, String>> verifyDomainInMailgun(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("verifyDomainInMailgun: " + domain);
        try {
            mailgunHandler.verifyMailgunDomain(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully verified"));
        } catch (IOException e) {
            logger.error("Error verifying domain in Mailgun: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/addDomainInSendgrid")
    public ResponseEntity<Map<String, String>> addDomainInSendgrid(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("addDomainInSendgrid: " + domain);
        try {
            sendgridHandler.addDomainInSendgrid(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully added"));
        } catch (IOException e) {
            logger.error("Error adding domain in Sendgrid: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/verifyDomainInSendgrid")
    public ResponseEntity<Map<String, String>> verifyDomainInSendgrid(@RequestBody Map<String, String> payload) throws IOException {
        String domain = payload.get("domain");
        logger.info("verifyDomainInSendgrid: " + domain);
        try {
            sendgridHandler.verifySendGridDomain(domain);
            return ResponseEntity.ok(Map.of("message", "Domain successfully verified"));
        } catch (IOException e) {
            logger.error("Error verifying domain in Sendgrid: " + e.getMessage());
            throw e;
        }
    }
}

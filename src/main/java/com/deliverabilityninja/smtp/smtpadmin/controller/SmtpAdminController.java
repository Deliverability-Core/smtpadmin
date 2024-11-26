package com.deliverabilityninja.smtp.smtpadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.deliverabilityninja.smtp.smtpadmin.dto.MailcowDomainDTO;
import com.deliverabilityninja.smtp.smtpadmin.model.Maildomain;
import com.deliverabilityninja.smtp.smtpadmin.services.MailcowDomainHandler;
import com.deliverabilityninja.smtp.smtpadmin.services.MailcowMailboxHandler;
import com.deliverabilityninja.smtp.smtpadmin.dto.MailboxDTO;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailDomainDAO;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailserverDAO;
import com.deliverabilityninja.smtp.smtpadmin.model.Mailserver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.deliverabilityninja.smtp.smtpadmin.model.Client;
import com.deliverabilityninja.smtp.smtpadmin.dao.ClientDAO;
import com.deliverabilityninja.smtp.smtpadmin.model.Mailbox;
import com.deliverabilityninja.smtp.smtpadmin.dao.MailboxDAO;
import com.deliverabilityninja.smtp.smtpadmin.dto.DomainListDTO;
import com.deliverabilityninja.smtp.smtpadmin.dao.IpPoolDAO;
import com.deliverabilityninja.smtp.smtpadmin.model.SMTPProvider;
import com.deliverabilityninja.smtp.smtpadmin.dto.AddIPPoolDTO;

import org.springframework.security.access.prepost.PreAuthorize;
@Controller
// @PreAuthorize("hasRole('ADMIN')")
public class SmtpAdminController {

    @Autowired
    private MailcowDomainHandler mailcowDomainHandler;

    @Autowired
    private MailcowMailboxHandler mailcowMailboxHandler;

    @Autowired
    private MailDomainDAO mailDomainDAO;

    @Autowired
    private MailserverDAO mailserverDAO;

    @Autowired
    private MailboxDAO mailboxDAO;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private IpPoolDAO ipPoolDAO;

    private Logger logger = LoggerFactory.getLogger(SmtpAdminController.class);

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/addDomain")
    public String addDomain(Model model) {
        List<Mailserver> mailservers = mailserverDAO.getAllMailServers();
        logger.info("mailservers: " + mailservers.toString());
        model.addAttribute("mailservers", mailservers);
        List<Client> clients = clientDAO.getClients();
        model.addAttribute("clients", clients);
        return "addDomain";
    }

    @GetMapping("/addMailbox")
    public String addMailbox(Model model) {
        List<Client> clients = clientDAO.getClients();
        model.addAttribute("clients", clients);
        return "addMailbox";
    }

    @PostMapping("/addDomain")
    public ResponseEntity<Map<String, String>> addDomainPost(
            @RequestParam("domain") String domain,
            @RequestParam("server_id") long server_id,
            @RequestParam("client_id") long client_id) {

        MailcowDomainDTO data = new MailcowDomainDTO();
        data.setDomain(domain);
        data.setServer_id(server_id);
        data.setClient_id(client_id);
        logger.info("addDomainPost: " + data.toString());

        try {
            String result = mailcowDomainHandler.addDomain(data);
            if (result.equals("SuccessMessage")) {
                return ResponseEntity.ok(Map.of("message", "Domain added successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Failed to add domain"));
            }
        } catch (Exception e) {
            logger.error("Error adding domain: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error adding domain: " + e.getMessage()));
        }
    }

    @PostMapping("/addMailbox")
    public ResponseEntity<?> addMailboxPost(
            @RequestParam("clientId") long clientId,
            @RequestParam("domain") List<Long> domain,
            @RequestParam("mailboxCount") int mailboxCount) {

        try {
            MailboxDTO data = new MailboxDTO();
            data.setClientId(clientId);
            data.setDomain(domain);
            data.setMailboxCount(mailboxCount);

            byte[] result = mailcowMailboxHandler.addMailbox(data);
            logger.info("addMailboxPost: Successfully generated mailboxes");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "mailboxes.csv");

            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error generating mailboxes: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error generating mailboxes: " + e.getMessage()));
        }
    }

    @GetMapping("/domains")
    public String listDomains(Model model) {
        List<DomainListDTO> domains = mailDomainDAO.getAllMailDomains();
        model.addAttribute("domains", domains);
        return "DomainList";
    }

    @GetMapping("/mailboxes")
    public String listMailboxes(@RequestParam(required = false) Long domainId, Model model) {
        List<Maildomain> domains = mailDomainDAO.getAllDomains();
        model.addAttribute("domains", domains);
        List<Mailbox> mailboxes = mailboxDAO.getMailboxes(domainId);
        model.addAttribute("mailboxes", mailboxes);

        return "EmailboxList";
    }

    @GetMapping("/addIpPool")
    public String addIpPool(Model model) {
        List<Maildomain> domains = mailDomainDAO.getAllDomains();
        model.addAttribute("domains", domains);
        return "addIpPool";
    }

    @GetMapping("/smtp-providers/{domainId}")
    public ResponseEntity<List<SMTPProvider>> getSmtpProviders(@PathVariable Long domainId) {
        List<SMTPProvider> smtpProviders = mailserverDAO.getSmtpProviders(domainId);
        return ResponseEntity.ok(smtpProviders);
    }

    @PostMapping("/addIpPool")
    public ResponseEntity<?> addIpPoolPost(@RequestBody AddIPPoolDTO addIPPoolDTO) {
        ipPoolDAO.addIpPool(addIPPoolDTO);
        return ResponseEntity.ok(Map.of("message", "IP pool added successfully"));
    }
}

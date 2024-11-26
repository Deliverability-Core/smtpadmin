package com.deliverabilityninja.smtp.smtpadmin.dto.Smtp2go;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMTP2GOResponse {
    private Data data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private List<DomainWrapper> domains;

        public List<DomainWrapper> getDomains() {
            return domains;
        }

        public void setDomains(List<DomainWrapper> domains) {
            this.domains = domains;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DomainWrapper {
        private Domain domain;

        public Domain getDomain() {
            return domain;
        }

        public void setDomain(Domain domain) {
            this.domain = domain;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Domain {
        @JsonProperty("dkim_selector")
        private String dkimSelector;
        
        @JsonProperty("dkim_value")
        private String dkimValue;
        
        @JsonProperty("rpath_selector")
        private String rpathSelector;
        
        @JsonProperty("rpath_value")
        private String rpathValue;

        public String getDkimSelector() {
            return dkimSelector;
        }   

        public String getDkimValue() {
            return dkimValue;
        }

        public String getRpathSelector() {
            return rpathSelector;
        }

        public String getRpathValue() {
            return rpathValue;
        }

        public void setDkimSelector(String dkimSelector) {
            this.dkimSelector = dkimSelector;
        }

        public void setDkimValue(String dkimValue) {
            this.dkimValue = dkimValue;
        }

        public void setRpathSelector(String rpathSelector) {
            this.rpathSelector = rpathSelector;
        }

        public void setRpathValue(String rpathValue) {
            this.rpathValue = rpathValue;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

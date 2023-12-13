package com.cheernota.riaratingreport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchV2JsonDto implements ResearchJsonDto {
    @JsonProperty("style")
    private String style;
    @JsonProperty("results")
    private boolean results;
    @JsonProperty("data")
    private List<InfoData> data;
    @JsonProperty("footer")
    private Footer footer;
    @JsonProperty("timestamp")
    private long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoData {
        @JsonProperty("header")
        private String header;
        @JsonProperty("type")
        private String type;
        @JsonProperty("data")
        private List<String> data;
        @JsonProperty("format")
        private String format;
        @JsonProperty("update")
        private double update;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Footer {
        @JsonProperty("methods")
        private String methods;
        @JsonProperty("source")
        private String source;
    }
}

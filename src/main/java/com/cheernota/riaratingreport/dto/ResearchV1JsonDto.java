package com.cheernota.riaratingreport.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchV1JsonDto implements ResearchJsonDto {
    @JsonProperty("project")
    private Project project;
    @JsonProperty("slides")
    private List<Slide> slides;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        @JsonProperty("name")
        private String name;
        @JsonProperty("color")
        private String color;
        @JsonProperty("id")
        private String id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Slide {
        @JsonProperty("data")
        private SlideData data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlideData {
        @JsonProperty("app")
        private App app;
        @JsonProperty("lang")
        private String lang;
        @JsonProperty("color")
        private String color;
        @JsonProperty("headers")
        private List<SlideDataHeader> headers;
        @JsonProperty("data")
        private List<List<AppData>> appData;
        @JsonProperty("summary")
        private List<AppData> summary;
        @JsonProperty("text")
        private SlideDataText text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class App {
        @JsonProperty("map")
        private AppMap appMap;
        @JsonProperty("columns")
        private List<AppColumn> appColumns;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppMap {
        @JsonProperty("type")
        private String appMapType;
        @JsonProperty("regionColumnId")
        private Integer regionColumnId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppColumn {
        @JsonProperty("id")
        private Integer appColumnId;
        @JsonProperty("headerId")
        private Integer appHeaderId;
        @JsonProperty("desktop")
        private Boolean isDesktop;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlideDataHeader {
        @JsonProperty("id")
        private int headerId;
        @JsonProperty("text")
        private String headerText;
        @JsonProperty("type")
        private String headerType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppData {
        @JsonProperty("sort_val")
        private String dataSortVal;
        @JsonProperty("text")
        private String dataText;
        @JsonProperty("noformat")
        private Boolean isNoFormat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlideDataText {
        @JsonProperty("lang")
        private String lang;
        @JsonProperty("source_comment")
        private String sourceComment;
        @JsonProperty("source")
        private String source;
    }
}

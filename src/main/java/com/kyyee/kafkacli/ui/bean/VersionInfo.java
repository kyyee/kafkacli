package com.kyyee.kafkacli.ui.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class VersionInfo implements Serializable {

    private static final long serialVersionUID = 4637273116136790267L;

    /**
     * 当前版本
     */
    @JsonProperty("currentVersion")
    private String currentVersion;

    /**
     * 历史版本列表
     */
    @JsonProperty("versions")
    private List<Version> versions;

    @Data
    public static class Version implements Serializable {

        private static final long serialVersionUID = 4637273116136790268L;

        @JsonProperty("version")
        private String version;

        @JsonProperty("title")
        private String title;

        @JsonProperty("changelogs")
        private List<String> changelogs;

    }

}

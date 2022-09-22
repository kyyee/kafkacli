package com.kyyee.kafkacli.ui.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 版本概要
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2019/4/20.
 */
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
    // todo 修改name
    @JsonProperty("versions")
    private List<Version> versions;

    /**
     * <pre>
     * 版本类
     * </pre>
     *
     * @author <a href="https://github.com/rememberber">RememBerBer</a>
     * @since 2019/4/20.
     */
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

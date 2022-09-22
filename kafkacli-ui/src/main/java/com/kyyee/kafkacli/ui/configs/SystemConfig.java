package com.kyyee.kafkacli.ui.configs;

import java.io.File;


public class SystemConfig {
    private static final String USER_HOME = System.getProperty("user.home");

    public static final String APP_HOME = USER_HOME + File.separator + ".kafkacli";

    public static final String USER_CONFIG_PATH = APP_HOME;

    public static final String DB_FILE_PATH = APP_HOME;

    /**
     * 日志文件路径
     */
    public final static String LOG_PATH = APP_HOME + File.separator + "logs";

}

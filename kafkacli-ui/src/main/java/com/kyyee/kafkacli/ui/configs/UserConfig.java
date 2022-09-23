package com.kyyee.kafkacli.ui.configs;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import com.formdev.flatlaf.util.SystemInfo;

import java.io.File;


public class UserConfig {

    Setting setting;

    public void setProps(String key, String value) {
        setting.put(key, value);
    }

    public String getProps(String key) {
        return setting.get(key);
    }

    /**
     * 存盘
     */
    public void flush() {
        setting.store(SystemConfig.USER_CONFIG_PATH + File.separator + "config.setting");
    }

    private static UserConfig userConfig;

    public static UserConfig getInstance() {
        if (userConfig == null) {
            userConfig = new UserConfig();
        }
        return userConfig;
    }

    private UserConfig() {
        setting = new Setting(FileUtil.touch(SystemConfig.USER_CONFIG_PATH + File.separator + "config.setting"), CharsetUtil.CHARSET_UTF_8, false);
    }

    private boolean autoCheckUpdate;

    private boolean defaultMaxWindow;

    private boolean unifiedBackground;

    private String beforeVersion;

    private String theme;

    private String font;

    private int fontSize;

    private boolean httpUseProxy;

    private String httpProxyHost;

    private String httpProxyPort;

    private String httpProxyUserName;

    private String httpProxyPassword;

    /**
     * 菜单栏位置
     */
    private String menuBarPosition;

    /**
     * sql dialect
     */
    private String sqlDialect;

    private String accentColor;

    /**
     * 上次关闭前所在的tab
     */
    private int recentTabIndex;

    private String quickNoteFontName;

    private int quickNoteFontSize;

    private String jsonBeautyFontName;

    private int jsonBeautyFontSize;

    private String currentHostName;

    private int qrCodeSize;

    private String qrCodeErrorCorrectionLevel;

    private String qrCodeLogoPath;

    private String qrCodeSaveAsPath;

    private String qrCodeRecognitionImagePath;

    private String digestFilePath;

    private int randomNumDigit;

    private int randomStringDigit;

    private int randomPasswordDigit;

    private String calculatorInputExpress;

    private String dbFilePath;

    private String dbFilePathBefore;

    private String quickNoteExportPath;

    private String jsonBeautyExportPath;

    private String imageExportPath;

    private String hostExportPath;

    private String lastSelectedColor;

    private String colorTheme;

    private String colorCodeType;

    private String regexText;

    public boolean isAutoCheckUpdate() {
        return setting.getBool("autoCheckUpdate", "setting.common", true);
    }

    public void setAutoCheckUpdate(boolean autoCheckUpdate) {
        setting.putByGroup("autoCheckUpdate", "setting.common", String.valueOf(autoCheckUpdate));
    }

    public boolean isTray() {
        return setting.getBool("tray", "setting.common", true);
    }

    public void setTray(boolean tray) {
        setting.putByGroup("tray", "setting.common", String.valueOf(tray));
    }

    public String getLanguage() {
        return setting.getStr("language", "setting.common", "简体中文");
    }

    public void setLanguage(String language) {
        setting.putByGroup("language", "setting.common", language);
    }

    public boolean isDefaultMaxWindow() {
        return setting.getBool("defaultMaxWindow", "setting.normal", true);
    }

    public void setDefaultMaxWindow(boolean defaultMaxWindow) {
        setting.putByGroup("defaultMaxWindow", "setting.normal", String.valueOf(defaultMaxWindow));
    }

    public boolean isUnifiedBackground() {
        return setting.getBool("unifiedBackground", "setting.normal", true);
    }

    public void setUnifiedBackground(boolean unifiedBackground) {
        setting.putByGroup("unifiedBackground", "setting.normal", String.valueOf(unifiedBackground));
    }

    public int getRecentTabIndex() {
        return setting.getInt("recentTabIndex", "setting.common", 0);
    }

    public void setRecentTabIndex(int recentTabIndex) {
        setting.putByGroup("recentTabIndex", "setting.common", String.valueOf(recentTabIndex));
    }

    public String getBeforeVersion() {
        return setting.getStr("beforeVersion", "setting.common", "v0.0.0");
    }

    public void setBeforeVersion(String beforeVersion) {
        setting.putByGroup("beforeVersion", "setting.common", beforeVersion);
    }

    public String getTheme() {
        return setting.getStr("theme", "setting.appearance", "Flat Darcula");
    }

    public void setTheme(String theme) {
        setting.putByGroup("theme", "setting.appearance", theme);
    }

    public boolean getFontInit() {
        return setting.getBool("fontInit", "setting.appearance", false);
    }

    public void setFontInit(boolean fontInit) {
        setting.putByGroup("fontInit", "setting.appearance", String.valueOf(fontInit));
    }

    public String getFontFamily() {
        if (SystemInfo.isLinux) {
            return setting.getStr("fontName", "setting.appearance", "Noto Sans CJK HK");
        } else {
            return setting.getStr("fontName", "setting.appearance", "微软雅黑");
        }
    }

    public void setFontFamily(String fontFamily) {
        setting.putByGroup("fontFamily", "setting.appearance", fontFamily);
    }

    public int getFontSize() {
        return setting.getInt("fontSize", "setting.appearance", 12);
    }

    public void setFontSize(int fontSize) {
        setting.putByGroup("fontSize", "setting.appearance", String.valueOf(fontSize));
    }

    public String getCurrentHostName() {
        return setting.getStr("currentHostName", "func.host", "");
    }

    public void setCurrentHostName(String currentHostName) {
        setting.putByGroup("currentHostName", "func.host", currentHostName);
    }

    public String getMenuBarPosition() {
        return setting.getStr("menuBarPosition", "setting.custom", "上方");
    }

    public void setMenuBarPosition(String menuBarPosition) {
        setting.putByGroup("menuBarPosition", "setting.custom", menuBarPosition);
    }

    public String getAccentColor() {
        return setting.getStr("accentColor", "setting.quickNote", "Moo.accent.red");
    }

    public void setAccentColor(String accentColor) {
        setting.putByGroup("accentColor", "setting.quickNote", accentColor);
    }

    public String getDbFilePath() {
        return setting.getStr("dbFilePath", "func.advanced", SystemConfig.DB_FILE_PATH);
    }

    public void setDbFilePath(String dbFilePath) {
        setting.putByGroup("dbFilePath", "func.advanced", dbFilePath);
    }


    public String getUserConfigPath() {
        return setting.getStr("userConfigPath", "func.advanced", SystemConfig.USER_CONFIG_PATH);
    }

    public void setUserConfigPath(String userConfigPath) {
        setting.putByGroup("userConfigPath", "func.advanced", userConfigPath);
    }


    public String getDbFilePathBefore() {
        return setting.getStr("dbFilePathBefore", "func.advanced", SystemConfig.DB_FILE_PATH);
    }

    public void setDbFilePathBefore(String dbFilePathBefore) {
        setting.putByGroup("dbFilePathBefore", "func.advanced", dbFilePathBefore);
    }

    public String getUserConfigPathBefore() {
        return setting.getStr("userConfigPathBefore", "func.advanced", SystemConfig.USER_CONFIG_PATH);
    }

    public void setUserConfigPathBefore(String userConfigPathBefore) {
        setting.putByGroup("userConfigPathBefore", "func.advanced", userConfigPathBefore);
    }

    public String getQuickNoteExportPath() {
        return setting.getStr("quickNoteExportPath", "func.quickNote", "");
    }

    public void setQuickNoteExportPath(String quickNoteExportPath) {
        setting.putByGroup("quickNoteExportPath", "func.quickNote", quickNoteExportPath);
    }

    public String getHostExportPath() {
        return setting.getStr("hostExportPath", "func.host", "");
    }

    public void setHostExportPath(String hostExportPath) {
        setting.putByGroup("hostExportPath", "func.host", hostExportPath);
    }

    public String getLastSelectedColor() {
        return setting.getStr("lastSelectedColor", "func.colorBoard", "007AAE");
    }

    public void setLastSelectedColor(String lastSelectedColor) {
        setting.putByGroup("lastSelectedColor", "func.colorBoard", lastSelectedColor);
    }

    public String getColorTheme() {
        return setting.getStr("colorTheme", "func.colorBoard", "默认");
    }

    public void setColorTheme(String colorTheme) {
        setting.putByGroup("colorTheme", "func.colorBoard", colorTheme);
    }

    public String getColorCodeType() {
        return setting.getStr("colorCodeType", "func.colorBoard", "HTML");
    }

    public void setColorCodeType(String colorCodeType) {
        setting.putByGroup("colorCodeType", "func.colorBoard", colorCodeType);
    }
}

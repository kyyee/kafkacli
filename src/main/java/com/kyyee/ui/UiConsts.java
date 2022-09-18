package com.kyyee.ui;

import java.awt.*;

/**
 * <pre>
 * UI相关的常量
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2019/8/10.
 */
public class UiConsts {

    /**
     * 软件名称,版本
     */
    public static final String APP_NAME = "KafkaCli";
    public static final String APP_VERSION = "v1.0.0";

    public static final int TABLE_ROW_HEIGHT = 36;

    /**
     * Logo-1024*1024
     */
    public static final Image IMAGE_LOGO_1024 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-1024.png"));

    /**
     * Logo-512*512
     */
    public static final Image IMAGE_LOGO_512 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-512.png"));

    /**
     * Logo-256*256
     */
    public static final Image IMAGE_LOGO_256 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-256.png"));

    /**
     * Logo-128*128
     */
    public static final Image IMAGE_LOGO_128 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-128.png"));

    /**
     * Logo-64*64
     */
    public static final Image IMAGE_LOGO_64 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-64.png"));

    /**
     * Logo-48*48
     */
    public static final Image IMAGE_LOGO_48 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-48.png"));

    /**
     * Logo-32*32
     */
    public static final Image IMAGE_LOGO_32 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-32.png"));

    /**
     * Logo-24*24
     */
    public static final Image IMAGE_LOGO_24 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-24.png"));

    /**
     * Logo-16*16
     */
    public static final Image IMAGE_LOGO_16 = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/logo-16.png"));

    /**
     * 帮助图标
     */
    public static final Image HELP_ICON = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/helpButton.png"));

    /**
     * 帮助图标-focused
     */
    public static final Image HELP_FOCUSED_ICON = Toolkit.getDefaultToolkit()
            .getImage(UiConsts.class.getResource("/icon/helpButtonFocused.png"));

    /**
     * 软件版本检查url
     */
    public static final String CHECK_VERSION_URL = "https://gitee.com/zhoubochina/MooTool/raw/master/src/main/resources/version_summary.json";

    /**
     * 软件下载链接信息url
     */
    public static final String DOWNLOAD_LINK_INFO_URL = "https://gitee.com/zhoubochina/MooTool/raw/develop/download_links.json";

    /**
     * 二维码url
     */
    public static final String QR_CODE_URL = "http://download.zhoubochina.com/file/mootool_qrcode.json";

    /**
     * 介绍二维码URL
     */
    public static final String INTRODUCE_QRCODE_URL = "http://download.zhoubochina.com/qrcode/introduce-mootool-qrcode.png";

}

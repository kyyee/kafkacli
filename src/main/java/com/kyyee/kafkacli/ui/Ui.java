package com.kyyee.kafkacli.ui;

import cn.hutool.core.thread.ThreadUtil;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.util.SystemInfo;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.dialog.AboutDialog;
import com.kyyee.kafkacli.ui.dialog.FontSizeGuideDialog;
import com.kyyee.kafkacli.ui.dialog.SettingDialog;
import com.kyyee.kafkacli.ui.form.LoadingForm;
import com.kyyee.kafkacli.ui.form.MainForm;
import com.kyyee.kafkacli.ui.form.TopicForm;
import com.kyyee.kafkacli.ui.frame.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class Ui {
    private Ui() {
    }

    public static void initApp() {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "KafkaCli");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "KafkaCli");
            System.setProperty("apple.awt.application.appearance", "system");

            FlatDesktop.setAboutHandler(() -> {
                try {
                    AboutDialog dialog = new AboutDialog();

                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception exception) {
                    log.error("init AboutDialog failed. {}", exception.getMessage());
                    throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
                }
            });
            FlatDesktop.setPreferencesHandler(() -> {
                try {
                    SettingDialog dialog = new SettingDialog();

                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception exception) {
                    log.error("init SettingDialog failed. {}", exception.getMessage());
                    throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
                }
            });
            FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);
        }

        FlatLaf.registerCustomDefaultsSource("themes");
        initTheme();

        // install inspectors
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");

        App.mainFrame = new MainFrame();
        App.mainFrame.init();
        if (UserConfig.getInstance().isTray()) {
            initTray();
        }

        JPanel loadingPanel = LoadingForm.getInstance().getLoadingPanel();
        App.mainFrame.add(loadingPanel);
        App.mainFrame.pack();
        App.mainFrame.setVisible(true);

        if (UserConfig.getInstance().isDefaultMaxWindow()
                || Toolkit.getDefaultToolkit().getScreenSize().getWidth() <= 1366) {
            // 低分辨率下自动最大化窗口
            App.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        Upgrade.smoothUpgrade();

        App.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initGlobalFont();
        App.mainFrame.setContentPane(MainForm.getInstance().getMainPanel());
        if (UserConfig.getInstance().getRecentTabIndex() != 3
                && MainForm.getInstance().getMainTabbedPane().getTabCount() > UserConfig.getInstance().getRecentTabIndex()) {
            MainForm.getInstance().getMainTabbedPane().setSelectedIndex(UserConfig.getInstance().getRecentTabIndex());
        }
        MainForm.getInstance().init();
        initAllTab();
        initOthers();

        App.mainFrame.remove(loadingPanel);
        FontSizeGuideDialog.init();
    }

    /**
     * 初始化所有tab
     */
    public static void initAllTab() {
        ThreadUtil.execute(() -> TopicForm.getInstance().init());

        // 检查新版版
        if (UserConfig.getInstance().isAutoCheckUpdate()) {
            ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            threadPoolExecutor.scheduleAtFixedRate(() -> Upgrade.checkUpdate(true), 0, 24, TimeUnit.HOURS);
        }
    }


    /**
     * 其他初始化
     */
    public static void initOthers() {

    }


    /**
     * 初始化系统托盘
     */
    public static void initTray() {

        try {
            if (SystemTray.isSupported() && App.systemTray == null) {
                App.systemTray = SystemTray.getSystemTray();

                App.popupMenu = new PopupMenu();
                App.popupMenu.setFont(App.mainFrame.getContentPane().getFont());

                MenuItem openItem = new MenuItem("KafkaCli");
                MenuItem exitItem = new MenuItem("Exit");

                openItem.addActionListener(e -> showMainFrame());
                exitItem.addActionListener(e -> shutdown());

                App.popupMenu.add(openItem);
                App.popupMenu.addSeparator();
                App.popupMenu.add(exitItem);

                App.trayIcon = new TrayIcon(UiConsts.IMAGE_LOGO_64, "KafkaCli", App.popupMenu);
                App.trayIcon.setImageAutoSize(true);

                App.trayIcon.addActionListener(e -> {
                    App.mainFrame.setVisible(true);
                    App.mainFrame.setExtendedState(JFrame.NORMAL);
                    App.mainFrame.requestFocus();
                });
                App.trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1 -> {
                                showMainFrame();
                                log.debug("托盘图标左键事件");
                            }
                            case MouseEvent.BUTTON2 -> log.debug("托盘图标中键事件");
                            case MouseEvent.BUTTON3 -> log.debug("托盘图标右键事件");
                            default -> {
                            }
                        }
                    }
                });
                App.systemTray.add(App.trayIcon);
            }

        } catch (Exception exception) {
            log.error("init SettingDialog failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }
    }

    public static void showMainFrame() {
        App.mainFrame.setVisible(true);
        if (App.mainFrame.getExtendedState() == Frame.ICONIFIED) {
            App.mainFrame.setExtendedState(Frame.NORMAL);
        } else if (App.mainFrame.getExtendedState() == 7) {
            App.mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        App.mainFrame.requestFocus();
    }

    public static void initTheme() {
        try {
            switch (UserConfig.getInstance().getTheme()) {
                case "Default" -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                case "Flat Light" -> {
                    setAccentColor();
                    FlatLightLaf.setup();
                }
                case "Flat IntelliJ" -> {
                    setAccentColor();
                    FlatIntelliJLaf.setup();
                }
                case "Flat Dark" -> {
                    setAccentColor();
                    FlatDarkLaf.setup();
                }
                case "Dark purple" -> FlatDarkPurpleIJTheme.setup();
                case "IntelliJ Cyan" -> FlatCyanLightIJTheme.setup();
                case "IntelliJ Light" -> FlatLightFlatIJTheme.setup();
                case "Monocai" -> FlatMonocaiIJTheme.setup();
                case "Monokai Pro" -> {
                    FlatMonokaiProIJTheme.setup();
                    UIManager.put("Button.arc", 5);
                }
                case "One Dark" -> FlatOneDarkIJTheme.setup();
                case "Gray" -> FlatGrayIJTheme.setup();
                case "High contrast" -> FlatHighContrastIJTheme.setup();
                case "GitHub Dark" -> FlatGitHubDarkIJTheme.setup();
                case "Xcode-Dark" -> FlatXcodeDarkIJTheme.setup();
                case "Vuesion" -> FlatVuesionIJTheme.setup();
                default -> {
                    setAccentColor();
                    FlatDarculaLaf.setup();
                }
            }

            if (FlatLaf.isLafDark()) {
//                FlatSVGIcon.ColorFilter.getInstance().setMapper(color -> color.brighter().brighter());
            } else {
                FlatSVGIcon.ColorFilter.getInstance().setMapper(color -> color.darker().darker());
//                SwingUtilities.windowForComponent(App.mainFrame).repaint();
            }

            if (UserConfig.getInstance().isUnifiedBackground()) {
                UIManager.put("TitlePane.unifiedBackground", true);
            }

        } catch (Exception exception) {
            log.error("init theme failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }
    }

    private static void setAccentColor() {
        FlatLaf.setGlobalExtraDefaults((!Objects.equals(UserConfig.getInstance().getAccentColor(), SettingDialog.accentColorKeys[0]))
                ? Collections.singletonMap("@accentColor", "$" + UserConfig.getInstance().getAccentColor())
                : null);
    }

    /**
     * 设置全局字体
     */
    public static void initGlobalFont() {
        if (!UserConfig.getInstance().getFontInit()) {
            // 根据DPI调整字号
            // 得到屏幕的分辨率dpi
            // dell 1920*1080/24寸=96
            // 小米air 1920*1080/13.3寸=144
            // 小米air 1366*768/13.3寸=96
            int fontSize = 12;
            // Mac等高分辨率屏幕字号初始化
            if (SystemInfo.isMacOS) {
                fontSize = 15;
            } else {
                fontSize = (int) (getScreenScale() * fontSize);
            }
            UserConfig.getInstance().setFontSize(fontSize);
        }

        FontUIResource resource = new FontUIResource(new Font(UserConfig.getInstance().getFontFamily(), Font.PLAIN, UserConfig.getInstance().getFontSize()));
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, resource);
            }
        }
    }

    /**
     * 获取屏幕规格
     * author by darcula@com.bulenkov
     * see https://github.com/bulenkov/Darcula
     *
     * @return
     */
    private static float getScreenScale() {
        int dpi = 96;

        try {
            dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (Exception exception) {
            log.error("get screen scale failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }

        float scale;
        if (dpi < 120) {
            scale = 1.0F;
        } else if (dpi < 144) {
            scale = 1.25F;
        } else if (dpi < 168) {
            scale = 1.5F;
        } else if (dpi < 192) {
            scale = 1.75F;
        } else {
            scale = 2.0F;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        log.info("screen dpi:{},width:{},height:{}", dpi, screenSize.getWidth(), screenSize.getHeight());
        return scale;
    }

    public static void shutdown() {
        saveBeforeExit();
//        App.sqlSession.close();
        App.mainFrame.dispose();
        System.exit(0);
    }

    public static void saveBeforeExit() {
        UserConfig.getInstance().setRecentTabIndex(MainForm.getInstance().getMainTabbedPane().getSelectedIndex());
        UserConfig.getInstance().flush();
    }
}

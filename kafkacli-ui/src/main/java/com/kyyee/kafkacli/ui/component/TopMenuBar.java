package com.kyyee.kafkacli.ui.component;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.Ui;
import com.kyyee.kafkacli.ui.Upgrade;
import com.kyyee.kafkacli.ui.configs.SystemConfig;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.dialog.*;
import com.kyyee.kafkacli.ui.form.MainForm;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 顶部菜单栏
 */
@Slf4j
public class TopMenuBar extends JMenuBar {

    private static TopMenuBar menuBar;

    private static JMenu themeMenu;

    private static JMenu fontFamilyMenu;

    private static JMenu fontSizeMenu;

    private static int initialThemeItemCount = -1;

    private static int initialFontFamilyItemCount = -1;

    private static int initialFontSizeItemCount = -1;

    private static final String[] themeNames = {
            "系统默认",
            "Flat Light",
            "Flat IntelliJ",
            "Flat Dark",
            "Flat Darcula(推荐)",
            "Dark purple",
            "IntelliJ Cyan",
            "IntelliJ Light",
            "Monocai",
            "Monokai Pro",
            "One Dark",
            "Gray",
            "High contrast",
            "GitHub Dark",
            "Xcode-Dark",
            "Vuesion"
    };

    public static String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    private static final String[] fontSizes = {
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26"};

    private TopMenuBar() {
    }

    public static TopMenuBar getInstance() {
        if (menuBar == null) {
            menuBar = new TopMenuBar();
        }
        return menuBar;
    }

    public void init() {
        TopMenuBar topMenuBar = getInstance();

        // ---------文件
        JMenu fileMenu = new JMenu("文件");
        // 设置
        JMenuItem newConnectionMenuItem = new JMenuItem("新建连接");
        newConnectionMenuItem.addActionListener(e -> newConnActionPerformed());
        fileMenu.add(newConnectionMenuItem);
        fileMenu.addSeparator();
        // 设置
        JMenuItem settingMenuItem = new JMenuItem("设置");
        settingMenuItem.addActionListener(e -> settingActionPerformed());
        fileMenu.add(settingMenuItem);
        // 退出
        JMenuItem exitMenuItem = new JMenuItem("退出");
        exitMenuItem.addActionListener(e -> exitActionPerformed());
        fileMenu.add(exitMenuItem);
        topMenuBar.add(fileMenu);

        // ---------外观
        JMenu appearanceMenu = new JMenu("外观");

        JCheckBoxMenuItem defaultMaxWindowMenuItem = new JCheckBoxMenuItem("默认最大化窗口");
        defaultMaxWindowMenuItem.setSelected(UserConfig.getInstance().isDefaultMaxWindow());
        defaultMaxWindowMenuItem.addActionListener(e -> {
            boolean selected = defaultMaxWindowMenuItem.isSelected();
            if (selected) {
                App.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                App.mainFrame.setExtendedState(JFrame.NORMAL);
            }
            UserConfig.getInstance().setDefaultMaxWindow(selected);
            UserConfig.getInstance().flush();
        });
        appearanceMenu.add(defaultMaxWindowMenuItem);

        JCheckBoxMenuItem unifiedBackgroundItem = new JCheckBoxMenuItem("窗口颜色沉浸式");
        unifiedBackgroundItem.setSelected(UserConfig.getInstance().isUnifiedBackground());
        unifiedBackgroundItem.addActionListener(e -> {
            boolean selected = unifiedBackgroundItem.isSelected();
            UserConfig.getInstance().setUnifiedBackground(selected);
            UserConfig.getInstance().flush();
            UIManager.put("TitlePane.unifiedBackground", selected);
            FlatLaf.updateUI();
        });
        appearanceMenu.add(unifiedBackgroundItem);
        appearanceMenu.addSeparator();

        themeMenu = new JMenu("主题风格");
        initThemesMenu();
        appearanceMenu.add(themeMenu);

        fontFamilyMenu = new JMenu("字体");
        fontFamilyMenu.setAutoscrolls(true);
        initFontFamilyMenu();
        appearanceMenu.add(fontFamilyMenu);

        fontSizeMenu = new JMenu("字号");
        initFontSizeMenu();
        appearanceMenu.add(fontSizeMenu);

        topMenuBar.add(appearanceMenu);

        // ---------帮助
        JMenu helpMenu = new JMenu("帮助");
        // 鼓励和支持
        JMenuItem supportMeMenuItem = new JMenuItem("鼓励和支持");
        supportMeMenuItem.addActionListener(e -> supportMeActionPerformed());
        helpMenu.add(supportMeMenuItem);

        helpMenu.addSeparator();

        // 查看日志
        JMenuItem logMenuItem = new JMenuItem("查看日志");
        logMenuItem.addActionListener(e -> logActionPerformed());
        helpMenu.add(logMenuItem);
        // 系统环境变量
        JMenuItem sysEnvMenuItem = new JMenuItem("系统环境变量");
        sysEnvMenuItem.addActionListener(e -> sysEnvActionPerformed());
        helpMenu.add(sysEnvMenuItem);

        helpMenu.addSeparator();

        // 检查更新
        JMenuItem checkUpdateMenuItem = new JMenuItem("检查更新");
        checkUpdateMenuItem.addActionListener(e -> checkUpdateActionPerformed());
        helpMenu.add(checkUpdateMenuItem);
        // 关于
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        aboutMenuItem.addActionListener(e -> aboutActionPerformed());
        helpMenu.add(aboutMenuItem);

        topMenuBar.add(helpMenu);
    }

    private void newConnActionPerformed() {
        try {
            NewConnDialog dialog = new NewConnDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception exception) {
            log.error("open new connection dialog failed. {}", exception.getMessage());
        }
    }

    private void settingActionPerformed() {
        try {
            SettingDialog dialog = new SettingDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception exception) {
            log.error("modify setting failed. {}", exception.getMessage(), exception);
            throw BaseException.of(BaseErrorCode.FILE_READ_ERROR);
        }
    }

    private void exitActionPerformed() {
        Ui.shutdown();
    }

    private void supportMeActionPerformed() {
        try {
            SupportMeDialog dialog = new SupportMeDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception exception) {
            log.error("open support me dialog failed. {}", exception.getMessage());
        }
    }

    private void logActionPerformed() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(SystemConfig.LOG_PATH));
        } catch (Exception exception) {
            log.error("query logfile failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.FILE_READ_ERROR);
        }
    }

    private void sysEnvActionPerformed() {
        try {

            List<String> lines = new ArrayList<>();
            lines.add("------------System.getenv---------------");
            for (Map.Entry<String, String> envEntry : System.getenv().entrySet()) {
                lines.add(envEntry.getKey() + "=" + envEntry.getValue());
            }
            lines.add("------------System.getProperties---------------");
            Properties properties = System.getProperties();
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                lines.add(objectObjectEntry.getKey() + "=" + objectObjectEntry.getValue());
            }
            SystemEnvDialog dialog = new SystemEnvDialog();
            dialog.writeLines(lines);
            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception exception) {
            log.error("query system env failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.FILE_READ_ERROR);
        }
    }


    private void checkUpdateActionPerformed() {
        try {
            Upgrade.checkUpdate(false);
        } catch (Exception exception) {
            log.error("open update info dialog failed. {}", exception.getMessage());
            JOptionPane.showMessageDialog(App.mainFrame, "检查更新失败，请检查网络！", "网络错误", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void aboutActionPerformed() {
        try {
            AboutDialog dialog = new AboutDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception exception) {
            log.error("open about dialog failed. {}", exception.getMessage());
        }
    }

    private void initThemesMenu() {

        if (initialThemeItemCount < 0)
            initialThemeItemCount = themeMenu.getItemCount();
        else {
            // remove old items
            for (int i = themeMenu.getItemCount() - 1; i >= initialThemeItemCount; i--)
                themeMenu.remove(i);
        }
        for (String themeName : themeNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(themeName);
            item.setSelected(themeName.equals(UserConfig.getInstance().getTheme()));
            item.addActionListener(this::changeTheme);
            themeMenu.add(item);
        }
    }

    private void changeTheme(ActionEvent actionEvent) {
        try {
            String selectedThemeName = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            UserConfig.getInstance().setTheme(selectedThemeName);
            UserConfig.getInstance().flush();

            Ui.initTheme();

            if (FlatLaf.isLafDark()) {
                FlatSVGIcon.ColorFilter.getInstance().setMapper(color -> color.brighter().brighter());
            }

            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainForm.getInstance().getMainTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "部分细节重启应用后生效！\n\n", "成功", JOptionPane.INFORMATION_MESSAGE);

            initThemesMenu();

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "保存失败！\n\n" + exception.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
            log.error("change theme failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }
    }

    private void initFontFamilyMenu() {

        if (initialFontFamilyItemCount < 0)
            initialFontFamilyItemCount = fontFamilyMenu.getItemCount();
        else {
            // remove old items
            for (int i = fontFamilyMenu.getItemCount() - 1; i >= initialFontFamilyItemCount; i--)
                fontFamilyMenu.remove(i);
        }
        for (String font : fontFamilies) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(font);
            item.setSelected(font.equals(UserConfig.getInstance().getFontFamily()));
            item.addActionListener(this::changeFontFamily);
            fontFamilyMenu.add(item);
        }
    }

    private void changeFontFamily(ActionEvent actionEvent) {
        try {
            String selectedFamily = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            UserConfig.getInstance().setFontFamily(selectedFamily);
            UserConfig.getInstance().flush();

            Ui.initGlobalFont();
            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainForm.getInstance().getMainTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "部分细节重启应用后生效！\n\n", "成功", JOptionPane.INFORMATION_MESSAGE);
            initFontFamilyMenu();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "保存失败！\n\n" + exception.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
            log.error("change font family failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }
    }


    private void initFontSizeMenu() {

        if (initialFontSizeItemCount < 0)
            initialFontSizeItemCount = fontSizeMenu.getItemCount();
        else {
            // remove old items
            for (int i = fontSizeMenu.getItemCount() - 1; i >= initialFontSizeItemCount; i--)
                fontSizeMenu.remove(i);
        }
        for (String fontSize : fontSizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(fontSize);
            item.setSelected(fontSize.equals(String.valueOf(UserConfig.getInstance().getFontSize())));
            item.addActionListener(this::changeFontSize);
            fontSizeMenu.add(item);
        }
    }

    private void changeFontSize(ActionEvent actionEvent) {
        try {
            String selectedFontSize = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            UserConfig.getInstance().setFontSize(Integer.parseInt(selectedFontSize));
            UserConfig.getInstance().flush();

            Ui.initGlobalFont();
            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainForm.getInstance().getMainTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "部分细节重启应用后生效！\n\n", "成功", JOptionPane.INFORMATION_MESSAGE);

            initFontSizeMenu();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(MainForm.getInstance().getMainPanel(), "保存失败！\n\n" + exception.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
            log.error("change font size failed. {}", exception.getMessage());
            throw BaseException.of(BaseErrorCode.SYS_INIT_ERROR);
        }
    }

}

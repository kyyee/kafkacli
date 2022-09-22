package com.kyyee.kafkacli.ui.dialog;

import cn.hutool.core.io.FileUtil;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

@Slf4j
public class SettingListener {

    public static void addListener(SettingDialog dialog) {


        // call onCancel() when cross is clicked
        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });
        // call onCancel() on ESCAPE
        dialog.getContentPane().registerKeyboardAction(e -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.getButtonCancel().addActionListener(e -> dialog.dispose());

        dialog.getButtonOK().addActionListener(e -> {
            try {

                // 常规
                UserConfig.getInstance().setAutoCheckUpdate(dialog.getAutoCheckUpdateCheckBox().isSelected());
                UserConfig.getInstance().setTray(dialog.getTrayCheckBox().isSelected());
                UserConfig.getInstance().setLanguage(dialog.getLanguageComboBox().getSelectedItem().toString());
                UserConfig.getInstance().setFontFamily(dialog.getFontFamilyComboBox().getSelectedItem().toString());
//            .fontFamilyChanged(new ActionEvent(dialog, ActionEvent.ACTION_PERFORMED, dialog.getFontFamilyComboBox().getSelectedItem().toString()));


                // 使用习惯
                UserConfig.getInstance().setMenuBarPosition(dialog.getMenuBarPositionComboBox().getSelectedItem().toString());
                resetMenuBar();

                // 高级
                UserConfig.getInstance().setDbFilePath(dialog.getDbFilePathTextField().getText());
                UserConfig.getInstance().setUserConfigPath(dialog.getDbFilePathTextField().getText());

                UserConfig.getInstance().flush();

                doCopyBefore(dialog);

                JOptionPane.showMessageDialog(dialog.getContentPane(), "保存成功！\n\n需要重启MooTool生效", "成功", JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.showMessageDialog(dialog.getContentPane(), "KafkaCli即将关闭！\n\n关闭后需要手动再次打开", "KafkaCli即将关闭", JOptionPane.INFORMATION_MESSAGE);
                UserConfig.getInstance().flush();
                System.exit(0);

            } catch (Exception exception) {
                JOptionPane.showMessageDialog(dialog.getContentPane(), "保存失败！\n\n" + exception.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
                log.error("save setting failed. {}", exception.getMessage());
            }
        });


        // 高级
        dialog.getButtonDbFilePathOpen().addActionListener(e -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(new File(dialog.getDbFilePathTextField().getText()));
            } catch (Exception exception) {
                log.error("open db file path failed. {}", exception.getMessage());
                throw BaseException.of(BaseErrorCode.FILE_READ_ERROR);
            }
        });

        dialog.getButtonDbFilePathExplore().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(dialog.getDbFilePathTextField().getText());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(dialog.getContentPane())) {
                dialog.getDbFilePathTextField().setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        dialog.getButtonUserConfigPathOpen().addActionListener(e -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(new File(dialog.getUserConfigPathTextField().getText()));
            } catch (Exception exception) {
                log.error("open user config path failed. {}", exception.getMessage());
                throw BaseException.of(BaseErrorCode.FILE_READ_ERROR);
            }
        });

        dialog.getButtonUserConfigPathExplore().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(dialog.getUserConfigPathTextField().getText());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(dialog.getContentPane())) {
                dialog.getUserConfigPathTextField().setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

    }

    private static void resetMenuBar() {
//        QuickNoteForm.init();
//        JsonBeautyForm.init();
//        HostForm.init();
//        HttpRequestForm.init();
//        QrCodeForm.init();
//        CryptoForm.init();
    }

    private static void doCopyBefore(SettingDialog dialog) {
        try {
            String dbFilePath = dialog.getDbFilePathTextField().getText();

            // 复制之前的数据文件到新位置
            String dbFilePathBefore = UserConfig.getInstance().getDbFilePathBefore();
            if (StringUtils.isNotBlank(dbFilePath) && !dbFilePath.equals(dbFilePathBefore)) {
                FileUtil.copy(dbFilePathBefore + File.separator + "kafkacli.db", dbFilePath, false);
            }

//            MybatisUtil.setSqlSession(null);

            UserConfig.getInstance().setDbFilePathBefore(dbFilePath);

            String userConfigPath = dialog.getUserConfigPathTextField().getText();

            // 复制之前的数据文件到新位置
            String userConfigPathBefore = UserConfig.getInstance().getUserConfigPathBefore();
            if (StringUtils.isNotBlank(userConfigPath) && !userConfigPath.equals(userConfigPathBefore)) {
                FileUtil.copy(userConfigPathBefore + File.separator + "config.setting", userConfigPath, false);
            }

            UserConfig.getInstance().setUserConfigPathBefore(userConfigPath);
        } catch (Exception exception) {
            log.error("save setting failed. {}", exception.getMessage());
            JOptionPane.showMessageDialog(dialog.getContentPane(), "保存失败！\n\n" + exception.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}

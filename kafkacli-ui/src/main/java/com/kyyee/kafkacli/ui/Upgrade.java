package com.kyyee.kafkacli.ui;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.common.utils.JSON;
import com.kyyee.kafkacli.ui.bean.VersionInfo;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.dialog.UpdateInfoDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;


@Slf4j
public class Upgrade {

    public static void checkUpdate(boolean initCheck) {
        // 当前版本
        String localVersion = UiConsts.APP_VERSION;

        // 从github获取最新版本相关信息
        String versionStr = HttpUtil.get(UiConsts.CHECK_VERSION_URL, 3000);
        if (StringUtils.isEmpty(versionStr)) {
            if (!initCheck) {
                JOptionPane.showMessageDialog(App.mainFrame, "检查超时，请关注GitHub Release！", "网络错误", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        if (versionStr.contains("404: Not Found")) {
            if (!initCheck) {
                JOptionPane.showMessageDialog(App.mainFrame, "链接无效，请关注GitHub Release！", "链接无效", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        VersionInfo versionInfo = JSON.toBean(versionStr, VersionInfo.class);
        // 最新版本
        String remoteVersion = versionInfo.getCurrentVersion();

        if (remoteVersion.compareTo(localVersion) > 0) {
            // 版本明细列表
            List<VersionInfo.Version> versions = versionInfo.getVersions();
            // 当前版本索引
            int currentVersionIndex = IntStream.range(0, versions.size()).filter(i -> localVersion.equals(versions.get(i).getVersion())).findFirst().orElse(0);

            // 版本更新日志
            StringBuilder changelogBuilder = new StringBuilder("<h1>惊现新版本！立即下载？</h1>");
            VersionInfo.Version version;
            for (int i = versions.size() - 1; i >= currentVersionIndex + 1; i--) {
                version = versions.get(i);
                changelogBuilder.append("<h2>").append(version.getVersion()).append("</h2>");
                changelogBuilder.append("<b>").append(version.getTitle()).append("</b><br/>");
                version.getChangelogs().forEach(changelog -> changelogBuilder.append("<p>").append(changelog).append("</p>"));
            }
            String versionLog = changelogBuilder.toString();
            UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog();
            updateInfoDialog.setHtmlText(versionLog);
            updateInfoDialog.setNewVersion(remoteVersion);
            updateInfoDialog.pack();
            updateInfoDialog.setVisible(true);
        } else {
            if (!initCheck) {
                JOptionPane.showMessageDialog(App.mainFrame, "当前已经是最新版本！", "恭喜", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * 平滑升级
     * 涉及的版本更新脚本和sql方法尽量幂等，以免升级过程中由于断电死机等异常中断造成重复执行升级操作
     */
    public static void smoothUpgrade() {
        // 取得当前版本
        String localVersion = UiConsts.APP_VERSION;
        // 取得升级前版本
        String beforeVersion = UserConfig.getInstance().getBeforeVersion();

        if (localVersion.compareTo(beforeVersion) > 0) {
            // 如果两者不一致则执行平滑升级
            log.info("平滑升级开始");
            // 否则先执行db_init.sql更新数据库新增表
            try {
//                MybatisUtil.initDbFile();
            } catch (Exception e) {
                log.error("执行平滑升级时先执行db_init.sql操作失败", e);
                return;
            }

            // 然后取两个版本对应的索引
            String versionStr = FileUtil.readString(UiConsts.class.getResource("/version.json"), CharsetUtil.CHARSET_UTF_8);
            if (StringUtils.isEmpty(versionStr)) {
                JOptionPane.showMessageDialog(App.mainFrame, "版本信息获取失败，请联系作者！", "信息错误", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            VersionInfo versionInfo = JSON.toBean(versionStr, VersionInfo.class);

            // 版本明细列表
            List<VersionInfo.Version> versions = versionInfo.getVersions();
            // 当前版本索引
            int currentVersionIndex = IntStream.range(0, versions.size()).filter(i -> localVersion.equals(versions.get(i).getVersion())).findFirst().orElse(0);

            int beforeVersionIndex = IntStream.range(0, versions.size()).filter(i -> beforeVersion.equals(versions.get(i).getVersion())).findFirst().orElse(0);
            log.info("old version:{}", beforeVersion);
            log.info("new version:{}", localVersion);
            // 遍历索引范围
            beforeVersionIndex++;
            for (int i = beforeVersionIndex; i <= currentVersionIndex; i++) {
                log.info("更新版本索引{}开始", i);
                // 执行每个版本索引的更新内容，按时间由远到近
                // 取得resources:upgrade下对应版本的sql，如存在，则先执行sql进行表结构或者数据更新等操作
                String sqlFile = "/upgrade/" + i + ".sql";
                URL sqlFileUrl = UiConsts.class.getResource(sqlFile);
                if (sqlFileUrl != null) {
                    String sql = FileUtil.readString(sqlFileUrl, CharsetUtil.CHARSET_UTF_8);
                    try {
//                        MybatisUtil.executeSql(sql);
                    } catch (Exception e) {
                        log.error("执行索引为{}的版本对应的sql时异常", i, e);
                        if (!e.getMessage().contains("duplicate column") && !e.getMessage().contains("constraint")) {
                            return;
                        }
                    }
                }
                upgrade(i);
                log.info("更新版本索引{}结束", i);
            }

            // 升级完毕且成功，则赋值升级前版本号为当前版本
            UserConfig.getInstance().setBeforeVersion(localVersion);
            UserConfig.getInstance().flush();
            log.info("平滑升级结束");
        }
    }

    /**
     * 执行升级脚本
     *
     * @param versionIndex 版本索引
     */
    private static void upgrade(int versionIndex) {
        log.info("执行升级脚本开始，版本索引：{}", versionIndex);
        switch (versionIndex) {
            case 12:
                // 初始化随手记表中的字体和语法数据
            case 21:
                break;
            default:
        }
        log.info("执行升级脚本结束，版本索引：{}", versionIndex);
    }
}

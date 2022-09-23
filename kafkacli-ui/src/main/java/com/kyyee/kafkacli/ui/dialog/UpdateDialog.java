package com.kyyee.kafkacli.ui.dialog;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.formdev.flatlaf.util.SystemInfo;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.common.utils.JSON;
import com.kyyee.kafkacli.ui.UiConsts;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.Future;

@Getter
@Slf4j
public class UpdateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonHref;
    private JProgressBar downloadProgressBar;
    private JLabel statusLabel;

    private File downLoadFile;
    Future<?> downloadTask;

    public UpdateDialog() {
        super(App.mainFrame, "下载新版");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 600, 200);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        UpdateListener.addListener(this);

    }

    public void download(String version) {
        getButtonOK().setEnabled(false);
        downloadTask = ThreadUtil.execAsync(() -> {
            String fileUrl = "";
            // 从github获取最新版本相关信息
            String downloadLinkInfo = HttpUtil.get(UiConsts.DOWNLOAD_LINK_INFO_URL);
            if (StringUtils.isEmpty(downloadLinkInfo) || downloadLinkInfo.contains("404: Not Found")) {
                JOptionPane.showMessageDialog(App.mainFrame, "获取下载链接失败，请关注Github Release！", "网络错误", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else {
                JsonNode jsonNode = JSON.toJsonNode(downloadLinkInfo);
                if (SystemInfo.isWindows) {
                    fileUrl = jsonNode.path("windows").asText();
                } else if (SystemInfo.isMacOS) {
                    fileUrl = jsonNode.path("mac").asText();
                } else if (SystemInfo.isLinux) {
                    fileUrl = jsonNode.path("linux").asText();
                }
            }

            String fileName = FileUtil.getName(fileUrl);

            File tempDir = new File(FileUtil.getTmpDirPath() + "MooTool");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            FileUtil.clean(tempDir);
            downLoadFile = FileUtil.file(tempDir + File.separator + fileName);
            HttpUtil.downloadFile(fileUrl, FileUtil.touch(downLoadFile), new StreamProgress() {
                @Override
                public void start() {
                    statusLabel.setText("开始下载。。。。");
                }

                @Override
                public void progress(long total, long progressSize) {
                    if (getDownloadProgressBar().getMaximum() == 100) {
                        getDownloadProgressBar().setMaximum((int) total);
                    }
                    getDownloadProgressBar().setValue((int) progressSize);
                    statusLabel.setText("已下载：" + FileUtil.readableFileSize(progressSize));
                }

                @Override
                public void finish() {
                    statusLabel.setText("下载完成！");
                    getButtonOK().setEnabled(true);
                }
            });
        });
    }

}

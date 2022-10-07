package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

@Slf4j
public class UpdateListener {

    public static void addListener(UpdateDialog dialog) {
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

        dialog.getButtonOK().addActionListener(e -> {
            try {
                Desktop.getDesktop().open(dialog.getDownLoadFile());
                dialog.dispose();
                System.exit(0);
            } catch (Exception exception) {
                log.error("open download file failed. {}", exception.getMessage());
                throw BaseException.of(BaseErrorCode.FILE_OPEN_ERROR);
            }
        });
        dialog.getButtonCancel().addActionListener(e -> {
            dialog.dispose();
            if (ObjectUtils.isNotEmpty(dialog.getDownloadTask())
                    && !dialog.getDownloadTask().isCancelled()) {
                dialog.getDownloadTask().cancel(true);
            }
        });

        dialog.getButtonHref().addActionListener(e -> {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("https://github.com/kyyee/kafkacli/releases"));
            } catch (Exception exception) {
                log.error("open href failed. {}", exception.getMessage());
                throw BaseException.of(BaseErrorCode.CALLBACK_URL_ERROR);
            }
        });

    }
}

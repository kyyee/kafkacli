package com.kyyee.kafkacli.ui.frame;


import com.formdev.flatlaf.util.SystemInfo;
import com.kyyee.kafkacli.ui.Ui;
import com.kyyee.kafkacli.ui.form.MainForm;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainFrameListener {

    public static void addListeners(MainFrame mainFrame) {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Ui.saveBeforeExit();
                if (SystemInfo.isWindows) {
                    // 托盘显示
                    mainFrame.setVisible(false);
                } else {
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

        MainForm.getInstance().getMainPanel().registerKeyboardAction(e -> {
            mainFrame.setVisible(false);
            Ui.saveBeforeExit();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

}

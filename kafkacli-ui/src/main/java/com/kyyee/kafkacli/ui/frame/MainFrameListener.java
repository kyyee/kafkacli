package com.kyyee.kafkacli.ui.frame;


import com.formdev.flatlaf.util.SystemInfo;
import com.kyyee.kafkacli.ui.Ui;
import com.kyyee.kafkacli.ui.form.MainForm;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class MainFrameListener {

    public static void addListeners(MainFrame mainFrame) {
        mainFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

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

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }
        });

        MainForm.getInstance().getMainPanel().registerKeyboardAction(e -> {
            mainFrame.setVisible(false);
            Ui.saveBeforeExit();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

}

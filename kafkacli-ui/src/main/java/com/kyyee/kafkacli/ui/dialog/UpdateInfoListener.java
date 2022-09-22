package com.kyyee.kafkacli.ui.dialog;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
public class UpdateInfoListener {

    public static void addListener(UpdateInfoDialog dialog) {
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
            UpdateDialog updateDialog = new UpdateDialog();
            updateDialog.pack();
            updateDialog.download(dialog.getNewVersion());
            updateDialog.setVisible(true);
            dialog.dispose();

        });
        dialog.getButtonCancel().addActionListener(e -> dialog.dispose());

    }
}

package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;
import java.util.List;

@Getter
public class SystemEnvDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea contentTextArea;

    public SystemEnvDialog() {
        super(App.mainFrame, "系统环境变量");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.4, 0.7);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        SystemEnvListener.addListener(this);
    }

    public void writeLines(List<String> lines) {
        for (String line : lines) {
            getContentTextArea().append(line);
            getContentTextArea().append("\n");
        }
    }

}

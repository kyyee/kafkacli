package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;

@Getter
public class SupportMeDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;

    public SupportMeDialog() {
        super(App.mainFrame, "鼓励和支持");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.4, 0.7);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        SupportMeListener.addListener(this);

    }

}

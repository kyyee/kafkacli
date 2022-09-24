package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;

@Getter
public class NewConnDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonTest;
    private JTextField clusterNameTextField;
    private JComboBox<String> clusterVersionComboBox;
    private JTextField bootstrapServersTextField;
    private JScrollPane settingScrollPane;

    public NewConnDialog() {
        super(App.mainFrame, "新建连接");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.4, 0.3);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        NewConnListener.addListener(this);

    }

}

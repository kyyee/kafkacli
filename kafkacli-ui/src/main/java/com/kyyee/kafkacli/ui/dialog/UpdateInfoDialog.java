package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

@Getter
public class UpdateInfoDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextPane contentTextPane;

    private String newVersion;

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public UpdateInfoDialog() {
        super(App.mainFrame, "惊现新版本");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.4, 0.64);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        UpdateInfoListener.addListener(this);

    }

    public void setPlaneText(String planeText) {
        getContentTextPane().setContentType("text/plain; charset=utf-8");
        getContentTextPane().setText(planeText);
        getContentTextPane().setCaretPosition(0);
    }

    public void setHtmlText(String htmlText) {
        getContentTextPane().setContentType("text/html; charset=utf-8");
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        getContentTextPane().setEditorKit(htmlEditorKit);
        StyleSheet styleSheet = htmlEditorKit.getStyleSheet();
        styleSheet.addRule("h2{color:#FBC87A;}");
        styleSheet.addRule("body{font-family:" + getButtonOK().getFont().getName() + ";font-size:" + getButtonOK().getFont().getSize() + ";}");
        getContentTextPane().setText(htmlText);
        getContentTextPane().setCaretPosition(0);
    }

}

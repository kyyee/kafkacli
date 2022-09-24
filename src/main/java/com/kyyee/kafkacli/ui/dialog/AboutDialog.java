package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.UiConsts;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;

@Getter
public class AboutDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel logoLabel;
    private JScrollPane contentScrollPane;
    private JLabel versionLabel;
    private JLabel authorLabel;
    private JLabel issueLabel;
    private JLabel flatLafLabel;
    private JLabel darculaLabel;
    private JLabel springLabel;
    private JLabel vsCodeIconsLabel;
    private JLabel spsLinkLabel;
    private JLabel codeGitHubLabel;

    public AboutDialog() {
        super(App.mainFrame, "关于");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.5, 0.68);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        // 设置滚动条速度
        getContentScrollPane().getVerticalScrollBar().setUnitIncrement(14);
        getContentScrollPane().getHorizontalScrollBar().setUnitIncrement(14);
        getContentScrollPane().getVerticalScrollBar().setDoubleBuffered(true);
        getContentScrollPane().getHorizontalScrollBar().setDoubleBuffered(true);

        getVersionLabel().setText(UiConsts.APP_VERSION);

        AboutListener.addListener(this);

        getContentPane().updateUI();
    }
}

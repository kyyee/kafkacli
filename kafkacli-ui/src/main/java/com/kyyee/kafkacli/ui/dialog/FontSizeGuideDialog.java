package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.UiConsts;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

@Getter
public class FontSizeGuideDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;

    public FontSizeGuideDialog() {
        super(App.mainFrame, "字号初始化向导");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 400, 200, false);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        FontSizeGuideListener.addListener(this);

    }

    /**
     * 引导用户调整字号
     */
    public static void init() {
        if (StringUtils.isEmpty(UserConfig.getInstance().getProps(UiConsts.FONT_SIZE_GUIDE))) {
            FontSizeGuideDialog fontSizeAdjustDialog = new FontSizeGuideDialog();
            fontSizeAdjustDialog.pack();
            fontSizeAdjustDialog.setVisible(true);
        }

        UserConfig.getInstance().setProps(UiConsts.FONT_SIZE_GUIDE, "true");
        UserConfig.getInstance().flush();
    }

}

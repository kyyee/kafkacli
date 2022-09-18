package com.kyyee.ui.frame;

import com.kyyee.ui.UiConsts;
import com.kyyee.ui.component.TopMenuBar;
import com.kyyee.ui.utils.ComponentUtils;

import javax.swing.*;

/**
 * <pre>
 * 主窗口
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">Zhou Bo</a>
 * @since 2019/8/10.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = -332963894416012132L;

    public static TopMenuBar topMenuBar;

    public void init() {
        this.setName(UiConsts.APP_NAME);
        this.setTitle(UiConsts.APP_NAME);
//        FrameUtil.setFrameIcon(this);

        topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.8, 0.88);
//        FrameListener.addListeners();
    }

}

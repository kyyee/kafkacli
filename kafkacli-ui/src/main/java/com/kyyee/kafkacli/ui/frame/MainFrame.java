package com.kyyee.kafkacli.ui.frame;

import com.kyyee.kafkacli.ui.UiConsts;
import com.kyyee.kafkacli.ui.component.TopMenuBar;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;

import javax.swing.*;
import java.util.ArrayList;


public class MainFrame extends JFrame {

    public void init() {
        setName(UiConsts.APP_NAME);
        setTitle(UiConsts.APP_NAME);

//        FrameUtil.setFrameIcon(this);
        setIconImages(new ArrayList<>() {
            {
                add(UiConsts.IMAGE_LOGO_64);
                add(UiConsts.IMAGE_LOGO_128);
                add(UiConsts.IMAGE_LOGO_256);
            }
        });

        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.8, 0.88);

        MainFrameListener.addListeners(this);
    }

}

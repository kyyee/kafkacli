package com.kyyee.kafkacli.ui.utils;

import com.kyyee.kafkacli.App;

import java.awt.*;

public class ComponentUtils {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(App.mainFrame.getGraphicsConfiguration());

    private static final int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;

    private static final int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;

    public static void setPreferSizeAndLocateToCenter(Component component, int preferWidth, int preferHeight) {
        setPreferSizeAndLocateToCenter(component, preferWidth, preferHeight, true);
    }

    /**
     * 设置组件preferSize并定位于屏幕中央
     */
    public static void setPreferSizeAndLocateToCenter(Component component, int preferWidth, int preferHeight, boolean modifySize) {
        component.setBounds((screenWidth - preferWidth) / 2, (screenHeight - preferHeight) / 2,
                preferWidth, preferHeight);
        if (modifySize) {
            Dimension preferSize = new Dimension(preferWidth, preferHeight);
            component.setPreferredSize(preferSize);
        }
    }
    public static void setPreferSizeAndLocateToCenter(Component component, double preferWidthPercent, double preferHeightPercent) {
        setPreferSizeAndLocateToCenter(component, preferWidthPercent, preferHeightPercent, true);
    }

    /**
     * 设置组件preferSize并定位于屏幕中央(基于屏幕宽高的百分百)
     */
    public static void setPreferSizeAndLocateToCenter(Component component, double preferWidthPercent, double preferHeightPercent, boolean modifySize) {
        int preferWidth = (int) (screenWidth * preferWidthPercent);
        int preferHeight = (int) (screenHeight * preferHeightPercent);
        setPreferSizeAndLocateToCenter(component, preferWidth, preferHeight, modifySize);
    }
}

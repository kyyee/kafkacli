package com.kyyee.ui.utils;

import com.kyyee.App;

import java.awt.*;

public class ComponentUtils {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(App.mainFrame.getGraphicsConfiguration());

    private static final int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;

    private static final int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;

    /**
     * 设置组件preferSize并定位于屏幕中央
     */
    public static void setPreferSizeAndLocateToCenter(Component component, int preferWidth, int preferHeight) {
        component.setBounds((screenWidth - preferWidth) / 2, (screenHeight - preferHeight) / 2,
                preferWidth, preferHeight);
        Dimension preferSize = new Dimension(preferWidth, preferHeight);
        component.setPreferredSize(preferSize);
    }

    /**
     * 设置组件preferSize并定位于屏幕中央(基于屏幕宽高的百分百)
     */
    public static void setPreferSizeAndLocateToCenter(Component component, double preferWidthPercent, double preferHeightPercent) {
        int preferWidth = (int) (screenWidth * preferWidthPercent);
        int preferHeight = (int) (screenHeight * preferHeightPercent);
        setPreferSizeAndLocateToCenter(component, preferWidth, preferHeight);
    }
}

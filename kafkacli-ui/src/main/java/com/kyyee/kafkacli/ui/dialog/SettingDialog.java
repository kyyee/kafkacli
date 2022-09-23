package com.kyyee.kafkacli.ui.dialog;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.component.TopMenuBar;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;

@Getter
public class SettingDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane contentScrollPane;
    private JCheckBox autoCheckUpdateCheckBox;
    private JComboBox<String> languageComboBox;
    private JComboBox<String> fontFamilyComboBox;
    private JPanel accentColorPanel;
    private JCheckBox trayCheckBox;
    private JComboBox<?> menuBarPositionComboBox;
    private JTextField dbFilePathTextField;
    private JButton buttonDbFilePathExplore;
    private JButton buttonDbFilePathOpen;
    private JTextField userConfigPathTextField;
    private JButton buttonUserConfigPathExplore;
    private JButton buttonUserConfigPathOpen;
    private JToolBar accentColorToolBar;

    public final static String[] accentColorKeys = {
        "Moo.accent.default",
        "Moo.accent.blue",
        "Moo.accent.purple",
        "Moo.accent.red",
        "Moo.accent.orange",
        "Moo.accent.yellow",
        "Moo.accent.green",
        "Moo.accent.mooYellow"
    };

    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];

    public SettingDialog() {
        super(App.mainFrame, "设置");
        ComponentUtils.setPreferSizeAndLocateToCenter(this, 0.5, 0.68);
        super.setContentPane(getContentPane());
        setModal(true);
        getRootPane().setDefaultButton(getButtonOK());

        SettingListener.addListener(this);

        // 设置滚动条速度
        getContentScrollPane().getVerticalScrollBar().setUnitIncrement(14);
        getContentScrollPane().getHorizontalScrollBar().setUnitIncrement(14);
        getContentScrollPane().getVerticalScrollBar().setDoubleBuffered(true);
        getContentScrollPane().getHorizontalScrollBar().setDoubleBuffered(true);

        initAccentColors();

        // 常规
        getAutoCheckUpdateCheckBox().setSelected(UserConfig.getInstance().isAutoCheckUpdate());
        getTrayCheckBox().setSelected(UserConfig.getInstance().isTray());
        getLanguageComboBox().setSelectedItem(UserConfig.getInstance().getLanguage());
        initFontFamilyMenu();
        getFontFamilyComboBox().setSelectedItem(UserConfig.getInstance().getFontFamily());

        // 使用习惯
        getMenuBarPositionComboBox().setSelectedItem(UserConfig.getInstance().getMenuBarPosition());

        // 高级
        getDbFilePathTextField().setText(UserConfig.getInstance().getDbFilePath());
        getUserConfigPathTextField().setText(UserConfig.getInstance().getUserConfigPath());

        contentPane.updateUI();
    }

    private void initFontFamilyMenu() {
        getFontFamilyComboBox().removeAllItems();
        for (String font : TopMenuBar.fontFamilies) {
            getFontFamilyComboBox().addItem(font);
        }
    }


    private void initAccentColors() {
        accentColorToolBar = new JToolBar();

        getAccentColorToolBar().add(Box.createHorizontalGlue());

        ButtonGroup buttonGroup = new ButtonGroup();
        int selectedIndex = 0;
        for (int i = 0; i < getAccentColorButtons().length; i++) {
            String accentColorKey = accentColorKeys[i];
            getAccentColorButtons()[i] = new JToggleButton(new AccentColorIcon(accentColorKey));
            getAccentColorButtons()[i].setToolTipText("仅FlatLight、FlatDark、FlatIntelliJ、FlatDarcula主题支持设置强调色");
            getAccentColorButtons()[i].addActionListener(this::accentColorChanged);
            if (accentColorKey.equals(UserConfig.getInstance().getAccentColor())) {
                selectedIndex = i;
            }
            getAccentColorToolBar().add(getAccentColorButtons()[i]);
            buttonGroup.add(getAccentColorButtons()[i]);
        }

        getAccentColorButtons()[selectedIndex].setSelected(true);

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName()))
                updateAccentColorButtons();
        });
        updateAccentColorButtons();

        getAccentColorPanel().add(getAccentColorToolBar());
    }

    /**
     * codes are copied from FlatLaf/flatlaf-demo/ (https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-demo)
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p>
     * https://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    private void accentColorChanged(ActionEvent e) {
        String accentColor = accentColorKeys[0];

        for (int i = 0; i < getAccentColorButtons().length; i++) {
            if (getAccentColorButtons()[i].isSelected()) {
                accentColor = accentColorKeys[i];
                UserConfig.getInstance().setAccentColor(accentColor);
                UserConfig.getInstance().flush();
                break;
            }
        }

        FlatLaf.setGlobalExtraDefaults((!accentColor.equals(accentColorKeys[0]))
            ? Collections.singletonMap("@accentColor", "$" + accentColor)
            : null);

        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (InstantiationException | IllegalAccessException ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        } catch (InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * codes are copied from FlatLaf/flatlaf-demo/ (https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-demo)
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p>
     * https://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    private void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported =
            lafClass == FlatLightLaf.class ||
                lafClass == FlatDarkLaf.class ||
                lafClass == FlatIntelliJLaf.class ||
                lafClass == FlatDarculaLaf.class;

        for (int i = 0; i < getAccentColorButtons().length; i++)
            getAccentColorButtons()[i].setEnabled(isAccentColorSupported);
    }

    /**
     * codes are copied from FlatLaf/flatlaf-demo/ (https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-demo)
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p>
     * https://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    private static class AccentColorIcon
        extends FlatAbstractIcon {
        private final String colorKey;

        AccentColorIcon(String colorKey) {
            super(16, 16, null);
            this.colorKey = colorKey;
        }

        @Override
        protected void paintIcon(Component c, Graphics2D g) {
            Color color = UIManager.getColor(colorKey);
            if (color == null)
                color = Color.lightGray;
            else if (!c.isEnabled()) {
                color = FlatLaf.isLafDark()
                    ? ColorFunctions.shade(color, 0.5f)
                    : ColorFunctions.tint(color, 0.6f);
            }

            g.setColor(color);
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("确定");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("取消");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        contentScrollPane = new JScrollPane();
        panel3.add(contentScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, -1));
        contentScrollPane.setViewportView(panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        Font panel5Font = this.$$$getFont$$$(null, -1, -1, panel5.getFont());
        if (panel5Font != null) panel5.setFont(panel5Font);
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(null, "常规", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        autoCheckUpdateCheckBox = new JCheckBox();
        autoCheckUpdateCheckBox.setText("启动时自动检查更新");
        panel5.add(autoCheckUpdateCheckBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        trayCheckBox = new JCheckBox();
        trayCheckBox.setText("最小化到系统托盘");
        panel5.add(trayCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("语言");
        panel5.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageComboBox = new JComboBox();
        languageComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("简体中文");
        defaultComboBoxModel1.addElement("English");
        languageComboBox.setModel(defaultComboBoxModel1);
        panel5.add(languageComboBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("字体");
        panel5.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontFamilyComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        fontFamilyComboBox.setModel(defaultComboBoxModel2);
        panel5.add(fontFamilyComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("强调色");
        panel5.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        accentColorPanel = new JPanel();
        accentColorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel5.add(accentColorPanel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "使用习惯", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setText("菜单栏位置");
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        menuBarPositionComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("顶部");
        defaultComboBoxModel3.addElement("底部");
        menuBarPositionComboBox.setModel(defaultComboBoxModel3);
        panel6.add(menuBarPositionComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel7, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(null, "高级", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        label5.setText("数据存储路径");
        panel7.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbFilePathTextField = new JTextField();
        panel7.add(dbFilePathTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonDbFilePathExplore = new JButton();
        buttonDbFilePathExplore.setText("...");
        panel7.add(buttonDbFilePathExplore, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonDbFilePathOpen = new JButton();
        buttonDbFilePathOpen.setText("打开");
        panel7.add(buttonDbFilePathOpen, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("本地配置路径");
        panel7.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        userConfigPathTextField = new JTextField();
        panel7.add(userConfigPathTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonUserConfigPathExplore = new JButton();
        buttonUserConfigPathExplore.setText("...");
        panel7.add(buttonUserConfigPathExplore, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUserConfigPathOpen = new JButton();
        buttonUserConfigPathOpen.setText("打开");
        panel7.add(buttonUserConfigPathOpen, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel7.add(spacer4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}

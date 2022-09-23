package com.kyyee.kafkacli.ui.dialog;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import com.kyyee.kafkacli.App;
import com.kyyee.kafkacli.ui.component.TopMenuBar;
import com.kyyee.kafkacli.ui.configs.UserConfig;
import com.kyyee.kafkacli.ui.utils.ComponentUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

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

}

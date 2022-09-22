package com.kyyee.kafkacli.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.kyyee.kafkacli.App;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClient;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

@Getter
public class MainForm {
    private JPanel mainPanel;
    private JTabbedPane mainTabbedPane;
    private JToolBar mainToolBar;
    private JButton clearButton;
    private JProgressBar loadProgressBar;
    private JLabel cache;
    private JTree clusterTree;
    private JPanel dataPanel;
    private JSplitPane browserSplitPane;
    private JPanel contentPanel;
    private JScrollBar dataScrollBar;
    private JButton createConnectButton;
    private static MainForm mainForm;

    private AdminClient adminClient;

    public void setAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public static MainForm getInstance() {
        if (mainForm == null) {
            mainForm = new MainForm();
        }
        return mainForm;
    }

    public void init() {
        getMainPanel().setVisible(true);
        getBrowserSplitPane().setDividerLocation(App.mainFrame.getWidth() / 5);
        //创建树节点

        DefaultTreeModel rootTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("cluster"), true);
        getClusterTree().setModel(rootTreeModel);
        getClusterTree().setEditable(true);

        getBrowserSplitPane().setLeftComponent(clusterTree);

        MainFormListener.addListener(this);

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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainToolBar = new JToolBar();
        mainPanel.add(mainToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainToolBar.add(spacer1);
        final Spacer spacer2 = new Spacer();
        mainToolBar.add(spacer2);
        final Spacer spacer3 = new Spacer();
        mainToolBar.add(spacer3);
        loadProgressBar = new JProgressBar();
        mainToolBar.add(loadProgressBar);
        final Spacer spacer4 = new Spacer();
        mainToolBar.add(spacer4);
        cache = new JLabel();
        cache.setText("0M/256M");
        mainToolBar.add(cache);
        clearButton = new JButton();
        clearButton.setText("Clear");
        mainToolBar.add(clearButton);
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(contentPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainTabbedPane = new JTabbedPane();
        contentPanel.add(mainTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        browserSplitPane = new JSplitPane();
        mainTabbedPane.addTab("Browser", browserSplitPane);
        clusterTree = new JTree();
        browserSplitPane.setLeftComponent(clusterTree);
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        browserSplitPane.setRightComponent(dataPanel);
        dataScrollBar = new JScrollBar();
        dataPanel.add(dataScrollBar, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        dataPanel.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        dataPanel.add(toolBar1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer6 = new Spacer();
        toolBar1.add(spacer6);
        createConnectButton = new JButton();
        createConnectButton.setText("新建链接");
        toolBar1.add(createConnectButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
package main.java.com.library.client.gui;

import main.java.com.library.client.gui.view.SideBar;
import main.java.com.library.client.gui.view.about.AboutPanel;
import main.java.com.library.client.gui.view.account.AccountPanel;
import main.java.com.library.client.gui.view.search.SearchPanel;
import main.java.com.library.client.gui.view.settings.SettingPanel;
import main.java.com.library.client.gui.view.workspace.WorkSpace;
import main.java.com.library.common.network.ResponsePack;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static main.java.com.library.client.gui.MainPage.mainFrame;
import static main.java.com.library.client.gui.impl.ToolsIMPL.setFormat;
import static main.java.com.library.client.gui.view.workspace.WorkSpace.scrollValue;
import static main.java.com.library.client.gui.view.workspace.WorkSpace.workPanel;

public class MainPanel extends JPanel {
    public static WorkSpace workSpace = null;
    public static SettingPanel settingPanel = null;
    public static AboutPanel aboutPanel = null;
    public static AccountPanel accountPanel = null;
    public static SearchPanel searchPanel = null;

    public MainPanel() {
        setLayout(new GridBagLayout());

        workSpace = new WorkSpace();
        settingPanel = new SettingPanel();
        aboutPanel = new AboutPanel();
        accountPanel = new AccountPanel();
        searchPanel = new SearchPanel();

        setFormat(new SideBar(), this,
                new Insets(0, 0, 0, 0), 0, 0, 0, 0, 10, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0, 0);
        setFormat(workSpace, this,
                new Insets(0, 0, 0, 0), 1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0, 0);
    }

    public void showWorkSpace(ResponsePack<?> responsePack, String action) throws IOException, ClassNotFoundException {
        removeComponents();
        mainFrame.setTitle("图书管理系统 - 工作区");
        if (responsePack != null) workPanel.unpackResponse(responsePack, action);
        WorkSpace.showTopPanel = action.equals("User") || action.equals("BorrowRecord");
        workPanel.updateLayout();
        SwingUtilities.invokeLater(() -> workPanel.getVerticalScrollBar().setValue(scrollValue));

        setFormat(workSpace, this,
                new Insets(0, 0, 0, 0), 1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, 0, 0);
        refresh();
    }

    public void showSearchPage() {
        removeComponents();
        mainFrame.setTitle("图书管理系统 - 搜索");
        setFormat(searchPanel, this, getDefault(), 0, 0);
        refresh();
    }

    public void showSettingPage() {
        removeComponents();
        mainFrame.setTitle("图书管理系统 - 设置");
        setFormat(settingPanel, this, getDefault(), 0, 0);
        refresh();
    }

    public void showAboutPage() {
        removeComponents();
        mainFrame.setTitle("图书管理系统 - 关于");
        setFormat(aboutPanel, this, getDefault(), 0, 0);
        refresh();
    }

    public void showAccountPage() {
        removeComponents();
        mainFrame.setTitle("图书管理系统 - 关于");
        setFormat(accountPanel, this, getDefault(), 0, 0);
        refresh();
    }

    public void removeComponents() {
        for (Component component : getComponents()) {
            if (!(component instanceof SideBar)) {
                remove(component);
            }
        }
        deleteAll(false);
        System.gc();

        workSpace = new WorkSpace();
        settingPanel = new SettingPanel();
        aboutPanel = new AboutPanel();
        accountPanel = new AccountPanel();
        searchPanel = new SearchPanel();
    }

    public GridBagConstraints getDefault() {
        return setFormat(null, null,
                new Insets(0, 0, 0, 0), 1, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, 0, 0);
    }

    public void refresh() {
        revalidate();
        repaint();
    }

    public void deleteAll(boolean isExit) {
        WorkSpace.deleteAll();
        searchPanel.deleteAll();
        accountPanel.deleteAll();

        workSpace.removeAll();
        settingPanel.removeAll();
        aboutPanel.removeAll();
        accountPanel.removeAll();
        searchPanel.removeAll();
        if (isExit) removeAll();

        workSpace = null;
        settingPanel = null;
        aboutPanel = null;
        accountPanel = null;
        searchPanel = null;
        System.gc();
    }
}
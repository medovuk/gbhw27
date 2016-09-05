package to.uk.ekbkloz.gbhw27.client.ui;

import to.uk.ekbkloz.gbhw27.client.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Andrey on 04.09.2016.
 */
public class UsersListBox implements UIComponent {
    private MainWindow mainWindow;
    private final Box usersListBox;
    private final JScrollPane usersListBoxSP;

    public UsersListBox(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        usersListBox = Box.createVerticalBox();
        usersListBoxSP = new JScrollPane(usersListBox);
        usersListBoxSP.setEnabled(false);
    }

    public void showUsersList(List<String> usersList) {
        usersListBox.removeAll();
        for (String user : usersList) {
            usersListBox.add(new Item(user));
        }
        usersListBoxSP.paintAll(usersListBoxSP.getGraphics());
    }

    public Box getUsersListBox() {
        return usersListBox;
    }

    @Override
    public Component getRootComponent() {
        return usersListBoxSP;
    }

    @Override
    public void setEnabled(boolean enabled) {
        usersListBoxSP.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible) {
        usersListBoxSP.setVisible(visible);
    }

    private class Item extends JLabel {
        public Item(String text) {
            super(text);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String prefix;
                    switch(e.getButton()) {
                        case 1:
                            prefix = "@" + getText() + ": ";
                            if (!mainWindow.getUserInputPanel().getText().startsWith(prefix)) {
                                mainWindow.getUserInputPanel().setText(prefix + mainWindow.getUserInputPanel().getText());
                                mainWindow.getUserInputPanel().grabFocus();
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            if (!getText().equals(mainWindow.getNickname())) {
                                prefix = ">>" + getText();
                                try {
                                    mainWindow.addRoomTab(prefix, true, getText());
                                } catch (IOException e1) {
                                    mainWindow.getLogger().error("При открытии вкладки приватного чата произошла ошибка", e1);
                                }
                                break;
                            }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }
    }
}

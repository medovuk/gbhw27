package to.uk.ekbkloz.gbhw27.client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Andrey on 04.09.2016.
 */
public class UsersListBox implements UIComponent {
    private final Box usersListBox;
    private final JScrollPane usersListBoxSP;

    public UsersListBox() {
        usersListBox = Box.createVerticalBox();
        usersListBoxSP = new JScrollPane(usersListBox);
        usersListBoxSP.setEnabled(false);
    }

    public void showUsersList(List<String> usersList) {
        usersListBox.removeAll();
        for (String user : usersList) {
            JLabel label = new JLabel(user);
            usersListBox.add(label);
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
}

package to.uk.ekbkloz.gbhw27.client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Andrey on 04.09.2016.
 */
public class UsersListBox {
    private final Box usersListBox = Box.createVerticalBox();
    private final JScrollPane usersListBoxSP = new JScrollPane(usersListBox);

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

    public JScrollPane getRootComponent() {
        return usersListBoxSP;
    }
}

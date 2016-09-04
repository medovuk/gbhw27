package to.uk.ekbkloz.gbhw27.client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Andrey on 04.09.2016.
 */
public class RoomsListBox {
    private final Box roomsListBox;
    private final JScrollPane roomsListBoxSP;
    private final JPanel rootComponent;
    private final JPanel roomAdditionPanel;
    private final JTextField roomNameInput;
    private final JButton addRoomButton;

    public RoomsListBox() {
        rootComponent = new JPanel();
        rootComponent.setLayout(new BorderLayout());
        roomAdditionPanel = new JPanel();
        roomAdditionPanel.setLayout(new BorderLayout());


        roomsListBox = Box.createVerticalBox();
        roomsListBoxSP = new JScrollPane(roomsListBox);
        rootComponent.add(roomsListBoxSP, BorderLayout.CENTER);

        roomNameInput = new JTextFieldWithHint(null, "Имя команты");
        roomAdditionPanel.add(roomNameInput, BorderLayout.CENTER);
        addRoomButton = new JButton("+");
        roomAdditionPanel.add(addRoomButton, BorderLayout.EAST);
        rootComponent.add(roomAdditionPanel, BorderLayout.SOUTH);
    }

    public void showRoomsList(List<String> roomsList) {
        roomsListBox.removeAll();
        for (String room : roomsList) {
            JLabel label = new JLabel(room);
            roomsListBox.add(label);
        }
        roomsListBoxSP.paintAll(roomsListBoxSP.getGraphics());
    }

    public JPanel getRootComponent() {
        return rootComponent;
    }
}

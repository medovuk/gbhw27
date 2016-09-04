package to.uk.ekbkloz.gbhw27.client.ui;

import to.uk.ekbkloz.gbhw27.client.MainWindow;
import to.uk.ekbkloz.gbhw27.proto.CreateRoom;
import to.uk.ekbkloz.gbhw27.proto.Packet;
import to.uk.ekbkloz.gbhw27.proto.PacketType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Created by Andrey on 04.09.2016.
 */
public class RoomsListBox implements UIComponent {
    private final MainWindow mainWindow;
    private final Box roomsListBox;
    private final JScrollPane roomsListBoxSP;
    private final JPanel rootComponent;
    private final JPanel roomAdditionPanel;
    private final JTextField roomNameInput;
    private final JButton addRoomButton;
    private final ActionListener action;

    public RoomsListBox(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainWindow.getConnectionHandler().isAuthenticated() && !roomNameInput.getText().isEmpty()) {
                    try {
                        mainWindow.getConnectionHandler().sendPacket(new Packet(PacketType.CREATE_CHATROOM, new CreateRoom(roomNameInput.getText())));
                        roomNameInput.setText("");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        rootComponent = new JPanel();
        rootComponent.setLayout(new BorderLayout());
        roomAdditionPanel = new JPanel();
        roomAdditionPanel.setLayout(new BorderLayout());


        roomsListBox = Box.createVerticalBox();
        roomsListBoxSP = new JScrollPane(roomsListBox);
        rootComponent.add(roomsListBoxSP, BorderLayout.CENTER);

        roomNameInput = new JTextFieldWithHint(null, "Имя команты");
        roomNameInput.addActionListener(action);
        roomAdditionPanel.add(roomNameInput, BorderLayout.CENTER);
        addRoomButton = new JButton("+");
        addRoomButton.addActionListener(action);
        roomAdditionPanel.add(addRoomButton, BorderLayout.EAST);
        rootComponent.add(roomAdditionPanel, BorderLayout.SOUTH);

        setEnabled(false);
    }

    public void showRoomsList(List<String> roomsList) {
        roomsListBox.removeAll();
        for (String room : roomsList) {
            JLabel label = new JLabel(room);
            roomsListBox.add(label);
        }
        roomsListBoxSP.paintAll(roomsListBoxSP.getGraphics());
    }

    @Override
    public Component getRootComponent() {
        return rootComponent;
    }

    @Override
    public void setEnabled(boolean enabled) {
        rootComponent.setEnabled(enabled);
        roomNameInput.setEnabled(enabled);
        addRoomButton.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible) {
        rootComponent.setVisible(visible);
    }
}

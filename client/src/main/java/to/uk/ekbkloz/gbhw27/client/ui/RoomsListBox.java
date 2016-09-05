package to.uk.ekbkloz.gbhw27.client.ui;

import to.uk.ekbkloz.gbhw27.client.MainWindow;
import to.uk.ekbkloz.gbhw27.proto.CreateRoom;
import to.uk.ekbkloz.gbhw27.proto.Packet;
import to.uk.ekbkloz.gbhw27.proto.PacketType;
import to.uk.ekbkloz.gbhw27.proto.RemoveRoom;
import to.uk.ekbkloz.gbhw27.proto.exceptions.NamingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
                        if (roomNameInput.getText().startsWith("@") || roomNameInput.getText().startsWith(">>")) {
                            throw new NamingException("имя комнаты не может начинаться с знака '@' или '>>'");
                        }
                        else {
                            mainWindow.getConnectionHandler().sendPacket(new Packet(PacketType.CREATE_CHATROOM, new CreateRoom(roomNameInput.getText())));
                            roomNameInput.setText("");
                        }
                    } catch (IOException | NamingException e1) {
                        JOptionPane.showMessageDialog(mainWindow, "Error: " + e1.getMessage());
                        mainWindow.getLogger().error("Ошибка добавления комнаты", e1);
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
            roomsListBox.add(new Item(room));
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

    private class Item extends JLabel {
        public Item(String text) {
            super(text);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch(e.getButton()) {
                        case 1:
                            try {
                                mainWindow.addRoomTab(getText(), false, null);
                            } catch (IOException e1) {
                                mainWindow.getLogger().error("При открытии вкладки произошла ошибка", e1);
                            }
                            break;
                        case 3:
                            try {
                                mainWindow.getConnectionHandler().sendPacket(new Packet(PacketType.REMOVE_CHATROOM, new RemoveRoom(getText())));
                            } catch (IOException e1) {
                                mainWindow.getLogger().error("При удалении команты произошла ошибка", e1);
                            }
                            break;
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

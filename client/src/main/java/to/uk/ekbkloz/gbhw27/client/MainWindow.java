package to.uk.ekbkloz.gbhw27.client;

import to.uk.ekbkloz.gbhw27.client.ui.*;
import to.uk.ekbkloz.gbhw27.proto.*;
import to.uk.ekbkloz.gbhw27.proto.exceptions.AuthException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.swing.*;

import static to.uk.ekbkloz.gbhw27.proto.PacketType.MESSAGE;

/**
 * Created by Andrey on 04.09.2016.
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 8479811883291471242L;
    private final static String SERVER_HOSTNAME = "localhost";
    private final static Integer SERVER_PORT = 8189;
    /**
     * Split панель с основным контентом
     */
    private final JSplitPane contentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    /**
     * split панель со списками пользователей и комнат
     */
    private final JSplitPane listsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final UsersListBox usersListBoxSP = new UsersListBox();
    private final RoomsListBox roomsListBoxSP = new RoomsListBox();
    private final JTabbedPane tabbedPanel = new JTabbedPane();

    private final Map<String, JTextArea> openedChatRooms = new HashMap<String, JTextArea>();

    private UserInputPanel userInputPanel = new UserInputPanel(this);

    /**
     * писака сообщений в чатик
     */
    private final Thread inputStreamListener;
    /**
     * панель для аутентификации
     */
    private final AuthPanel authPanel = new AuthPanel(this);

    private final ConnectionHandler connectionHandler;


    public MainWindow() throws HeadlessException, IOException {
        connectionHandler = new ConnectionHandler(SERVER_HOSTNAME, SERVER_PORT);
        inputStreamListener = new InputStreamListener(this);
        inputStreamListener.start();
        setTitle("Blah-blah chat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(300, 300, 400, 400);
        setLayout(new BorderLayout());

        contentPanel.add(tabbedPanel);
        contentPanel.add(listsPanel);

        listsPanel.add(usersListBoxSP.getRootComponent());
        listsPanel.add(roomsListBoxSP.getRootComponent());
        add(contentPanel, BorderLayout.CENTER);

        //панель для ввода сообщений
        add(userInputPanel.getRootComponent(), BorderLayout.SOUTH);

        //панель для аутентификации
        add(authPanel.getRootComponent(), BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                try {
                    connectionHandler.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (inputStreamListener != null) {
                    inputStreamListener.interrupt();
                    while(inputStreamListener.isAlive()) {}
                }
            }

        });

        setVisible(true);
    }

    public void processUserInput() {
        if (connectionHandler.isAuthenticated()) {
            try {
                connectionHandler.sendPacket(new Packet(PacketType.MESSAGE, new Message(tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex()), null, null, userInputPanel.getText())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInputPanel.setText("");
            userInputPanel.grabFocus();
        }
    }

    public void setAuthenticated(Boolean authenticated) {
        if (authenticated) {
            authPanel.setEnabled(false);
            authPanel.setVisible(false);
        }
        else {
            authPanel.setEnabled(true);
            authPanel.setVisible(true);
        }
    }

    public void addRoomTab(String roomName) {
        if (!openedChatRooms.containsKey(roomName)) {
            JTextArea chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            tabbedPanel.addTab(roomName, new JScrollPane(chatArea));
            openedChatRooms.put(roomName, chatArea);
        }
    }

    public void removeRoomTab(String roomName) {
        openedChatRooms.remove(roomName);
        tabbedPanel.removeTabAt(tabbedPanel.indexOfTab(roomName));
    }

    public void updateRoomsList(RoomsList roomsList) {
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public UsersListBox getUsersListBoxSP() {
        return usersListBoxSP;
    }

    public RoomsListBox getRoomsListBoxSP() {
        return roomsListBoxSP;
    }

    public UserInputPanel getUserInputPanel() {
        return userInputPanel;
    }

    public AuthPanel getAuthPanel() {
        return authPanel;
    }

    public Map<String, JTextArea> getOpenedChatRooms() {
        return openedChatRooms;
    }
}

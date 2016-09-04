package to.uk.ekbkloz.gbhw27.client;

import to.uk.ekbkloz.gbhw27.client.ui.*;
import to.uk.ekbkloz.gbhw27.proto.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;

/**
 * Created by Andrey on 04.09.2016.
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 8479811883291471242L;
    private final static String SERVER_HOSTNAME = "localhost";
    private final static Integer SERVER_PORT = 8189;
    public final static String TITLE = "Blah-blah chat";
    /**
     * Split панель с основным контентом
     */
    private final JSplitPane contentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    /**
     * split панель со списками пользователей и комнат
     */
    private final JSplitPane listsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final UsersListBox usersListBoxSP = new UsersListBox();
    private final RoomsListBox roomsListBoxSP = new RoomsListBox(this);
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

    private String nickname;


    public MainWindow() throws HeadlessException, IOException {
        connectionHandler = new ConnectionHandler(SERVER_HOSTNAME, SERVER_PORT);
        inputStreamListener = new InputStreamListener(this);
        inputStreamListener.start();
        setTitle(TITLE);
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
        if (connectionHandler.isAuthenticated() && !userInputPanel.getText().isEmpty()) {
            try {
                connectionHandler.sendPacket(new Packet(PacketType.MESSAGE, new Message(tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex()), getNickname(), null, userInputPanel.getText())));
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
            userInputPanel.setEnabled(true);
            userInputPanel.setVisible(true);
            usersListBoxSP.setEnabled(true);
            roomsListBoxSP.setEnabled(true);
        }
        else {
            authPanel.setEnabled(true);
            authPanel.setVisible(true);
            userInputPanel.setEnabled(false);
            userInputPanel.setVisible(false);
            usersListBoxSP.setEnabled(false);
            roomsListBoxSP.setEnabled(false);
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        setTitle(TITLE + " - " + this.nickname);
    }
}

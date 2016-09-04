package to.uk.ekbkloz.gbhw27.client;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private final Box usersListBox = Box.createVerticalBox();
    private final JScrollPane usersListBoxSP = new JScrollPane(usersListBox);
    private final Box roomsListBox = Box.createVerticalBox();
    private final JScrollPane roomsListBoxSP = new JScrollPane(roomsListBox);
    private final JTabbedPane tabbedPanel = new JTabbedPane();

    private final Map<String, JTextArea> chatRooms = new HashMap<String, JTextArea>();

    /**
     * панель для ввода сообщений
     */
    private final JPanel userInputPanel = new JPanel();

    /**
     * текстовое поле для ввода
     */
    private final JTextField userInputText = new JTextField();

    /**
     * кнопка для ввода
     */
    private final JButton userInputButton = new JButton();

    /**
     * сокет подключения к серверу
     */
    private Socket clientSocket = null;

    /**
     * скаенр входящего потока
     */
    private ObjectInputStream input = null;

    /**
     * писака в исходящий поток
     */
    private ObjectOutputStream output = null;
    /**
     * писака сообщений в чатик
     */
    private Thread inputStreamListener = null;
    /**
     * флаг аутентификации
     */
    private Boolean authenticated = false;
    /**
     * панель для аутентификации
     */
    private final JPanel authPanel = new JPanel();
    /**
     * Поле для ввода логина
     */
    private final JTextField loginField = new JTextField() {
        private String hint = "логин";

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(hint, 4, 16);
            }
        }
    };
    /**
     * Поле для ввода пароля
     */
    private final JPasswordField passwordField = new JPasswordField() {
        private String hint = "пароль";

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(hint, 4, 16);
            }
        }
    };
    /**
     * Кнопка для подтверждения аутентификации
     */
    private final JButton authButton = new JButton();


    public MainWindow() throws HeadlessException, IOException {
        setTitle("Blah-blah chat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(300, 300, 400, 400);
        setLayout(new BorderLayout());




        contentPanel.add(tabbedPanel);
        contentPanel.add(listsPanel);

        listsPanel.add(usersListBoxSP);
        listsPanel.add(roomsListBoxSP);
        add(contentPanel, BorderLayout.CENTER);

        //панель для ввода сообщений
        userInputPanel.setLayout(new BorderLayout());
        add(userInputPanel, BorderLayout.SOUTH);

        //текстовое поле для ввода
        userInputText.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                processUserInput();
            }

        });
        userInputPanel.add(userInputText, BorderLayout.CENTER);

        //кнопка для ввода
        userInputButton.setText("Ввод");
        userInputButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                processUserInput();
            }

        });
        userInputPanel.add(userInputButton, BorderLayout.EAST);

        //панель для аутентификации
        authPanel.setLayout(new GridLayout(1, 3));
        add(authPanel, BorderLayout.NORTH);

        loginField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    connect();
                }
                catch(IOException | AuthException | ClassNotFoundException e1) {
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + e1.getMessage());
                }
            }
        });
        authPanel.add(loginField);
        passwordField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    connect();
                }
                catch(IOException | AuthException | ClassNotFoundException e1) {
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + e1.getMessage());
                }
            }
        });
        authPanel.add(passwordField);
        authButton.setText("Вход");
        authButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    connect();
                }
                catch(IOException | AuthException | ClassNotFoundException e1) {
                    JOptionPane.showMessageDialog(contentPanel, "Error: " + e1.getMessage());
                }
            }
        });
        authPanel.add(authButton);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                if (inputStreamListener != null) {
                    inputStreamListener.interrupt();
                    while(inputStreamListener.isAlive()) {}
                }
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });

        setVisible(true);
    }

    private void processUserInput() {
        if (authenticated) {
            try {
                sendPacket(new Packet(PacketType.MESSAGE, new Message(tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex()), null, null, userInputText.getText())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInputText.setText("");
            userInputText.grabFocus();
        }
    }

    private void connect() throws IOException, AuthException, ClassNotFoundException {
        clientSocket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());

        sendPacket(new Packet(PacketType.AUTH_REQUEST, new Credential(loginField.getText(), new String(passwordField.getPassword()))));
        Packet packet = (Packet) input.readObject();
        if (PacketType.AUTH_RESPONSE.equals(packet.getType())) {
            AuthResponse authResponse = packet.getPayload(AuthResponse.class);
            setAuthenticated(authResponse.isSucceeded());
            if (!chatRooms.containsKey(authResponse.getRoomName())) {
                JTextArea chatArea = new JTextArea();
                chatArea.setEditable(false);
                chatArea.setLineWrap(true);
                chatArea.setWrapStyleWord(true);
                tabbedPanel.addTab(authResponse.getRoomName(), new JScrollPane(chatArea));
                chatRooms.put(authResponse.getRoomName(), chatArea);
            }
        }

        if (isAuthenticated() && (inputStreamListener == null || !inputStreamListener.isAlive())) {

            inputStreamListener = new Thread() {
                private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("HH:mm:ss | ");
                private LocalDateTime curTime;

                @Override
                public void run() {
                    Packet packet;
                    while(!isInterrupted()) {
                        try {
                            packet = (Packet) input.readObject();
                            switch (packet.getType()) {
                                case AUTH_REQUEST:
                                    break;
                                case AUTH_RESPONSE:
                                    break;
                                case USERS_LIST:
                                    UsersList usersList = packet.getPayload(UsersList.class);
                                    System.out.println(Arrays.deepToString(usersList.getList()));
                                    break;
                                case MESSAGE:
                                    curTime = LocalDateTime.now();
                                    Message message = packet.getPayload(Message.class);
                                    chatRooms.get(message.getRoom()).append(curTime.format(dtFormatter) + message.getFrom() + ": " + message.getText() + "\r\n");
                                    break;
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            interrupt();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            interrupt();
                        }
                    }
                }

                @Override
                public void interrupt() {
                    super.interrupt();
                    try {
                        input.close();
                        output.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthenticated(false);
                }
            };
            inputStreamListener.start();
        }
        else {
            throw new AuthException("Неверный логин или пароль!");
        }
    }

    private void sendPacket(Packet packet) throws IOException {
        try {
            output.writeObject(packet);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
        if (authenticated) {
            loginField.setEnabled(false);
            passwordField.setEnabled(false);
            authButton.setEnabled(false);
            authPanel.setEnabled(false);
            authPanel.setVisible(false);
        }
        else {
            loginField.setEnabled(true);
            passwordField.setEnabled(true);
            authButton.setEnabled(true);
            authPanel.setEnabled(true);
            authPanel.setVisible(true);
        }
    }

    private Boolean isAuthenticated() {
        return authenticated;
    }
}

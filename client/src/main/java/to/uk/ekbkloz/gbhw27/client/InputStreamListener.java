package to.uk.ekbkloz.gbhw27.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to.uk.ekbkloz.gbhw27.proto.Message;
import to.uk.ekbkloz.gbhw27.proto.Packet;
import to.uk.ekbkloz.gbhw27.proto.RoomsList;
import to.uk.ekbkloz.gbhw27.proto.UsersList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Andrey on 04.09.2016.
 */
public class InputStreamListener extends Thread {
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("HH:mm:ss | ");
    private LocalDateTime curTime;
    private final MainWindow mainWindow;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public InputStreamListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        Packet packet;
        while(!isInterrupted()) {
            try {
                if (mainWindow.getConnectionHandler().isAuthenticated()) {
                    packet = mainWindow.getConnectionHandler().receivePacket();
                    switch (packet.getType()) {
                        case AUTH_REQUEST:
                            break;
                        case AUTH_RESPONSE:
                            break;
                        case USERS_LIST:
                            UsersList usersList = packet.getPayload(UsersList.class);
                            mainWindow.getUsersLists().put(usersList.getChatRoom(), usersList.getList());
                            if (usersList.getChatRoom().equals(mainWindow.getTabbedPanel().getTitleAt(mainWindow.getTabbedPanel().getSelectedIndex()))) {
                                mainWindow.getUsersListBoxSP().showUsersList(usersList.getList());
                            }
                            break;
                        case MESSAGE:
                            curTime = LocalDateTime.now();
                            Message message = packet.getPayload(Message.class);
                            if (message.getTo() != null && !message.getTo().isEmpty() && message.getTo().equals(mainWindow.getNickname())) {
                                if (message.getTo().equals(message.getFrom())) {
                                    if (mainWindow.getOpenedPrivateRooms().containsKey(message.getRoom())) {
                                        mainWindow.getOpenedPrivateRooms().get(message.getRoom()).append(curTime.format(dtFormatter) + message.getFrom() + ": " + message.getText() + "\r\n");
                                    }
                                }
                                else {
                                    if (!mainWindow.getOpenedPrivateRooms().containsKey(">>" + message.getFrom())) {
                                        mainWindow.addRoomTab(">>" + message.getFrom(), true, message.getFrom());
                                    }
                                    mainWindow.getOpenedPrivateRooms().get(">>" + message.getFrom()).append(curTime.format(dtFormatter) + message.getFrom() + ": " + message.getText() + "\r\n");
                                }
                            }
                            else {
                                if (mainWindow.getOpenedChatRooms().containsKey(message.getRoom())) {
                                    mainWindow.getOpenedChatRooms().get(message.getRoom()).append(curTime.format(dtFormatter) + message.getFrom() + ": " + message.getText() + "\r\n");
                                }
                            }
                            break;
                        case ROOMS_LIST:
                            mainWindow.getRoomsListBoxSP().showRoomsList(packet.getPayload(RoomsList.class).getList());
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e.toString());
                e.printStackTrace();
                mainWindow.setAuthenticated(mainWindow.getConnectionHandler().isAuthenticated());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}

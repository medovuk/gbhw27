package to.uk.ekbkloz.gbhw27.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to.uk.ekbkloz.gbhw27.proto.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Andrey on 04.09.2016.
 */
public class Server extends Thread {
    public static final String MAIN_CHATROOM_NAME = "main";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ServerSocket serverSocket;
    private final Map<String, Set<ConnectionHandler>> rooms = new ConcurrentHashMap<String, Set<ConnectionHandler>>();
    private final Map<String, ConnectionHandler> connectionHandlers = new ConcurrentHashMap<String, ConnectionHandler>();

    public Server(int port) throws IOException, SQLException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        SQLHandler.connect();
        newChatRoom(MAIN_CHATROOM_NAME);
        logger.info("Server created");
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try{
                ConnectionHandler connHandler = new ConnectionHandler(serverSocket.accept(), this);
                connHandler.start();
            }
            catch (IOException e) {
                logger.error("Ошибка подключения", e);
            }
        }
        if (isInterrupted()) {
            logger.info(getClass().getSimpleName() + " was interrupted");
            try{
                serverSocket.close();
            }
            catch(IOException e){
                logger.error("При закрытии подключения произошла ошибка", e);
            }
        }
    }

    void sendMessage(ConnectionHandler connectionHandler, Message message) {
        Packet packet = new Packet(PacketType.MESSAGE, message);
        logger.debug(message.toString());
        if (message.getTo() != null && !message.getTo().isEmpty()) {
            if (connectionHandlers.containsKey(message.getTo())) {
                try {
                    connectionHandlers.get(message.getTo()).sendPacket(packet);
                    message.setTo(message.getFrom());
                    connectionHandler.sendPacket(packet);
                } catch (IOException e) {
                    logger.error("При отправке сообщения клиенту " + message.getTo() + " от клиента " + message.getFrom() + " произошла ошибка", e);
                }
            }
        }
        else if (message.getRoom() != null && !message.getRoom().isEmpty() && !MAIN_CHATROOM_NAME.equals(message.getRoom())) {
            if (rooms.containsKey(message.getRoom())) {
                //если комната была пересоздана с тем же именем и в ней нет отправителя
                if (connectionHandler != null) {
                    if (!rooms.get(message.getRoom()).contains(connectionHandler)) {
                        addToChatRoom(connectionHandler, message.getRoom());
                    }
                }
                broadcastPacket(packet, message.getRoom());
            }
        }
        else {
            broadcastPacket(packet);
        }
    }

    void removeHandler(ConnectionHandler connHandler) {
        connectionHandlers.remove(connHandler.getName());
        rooms.keySet().forEach(roomName -> removeFromChatRoom(connHandler, roomName));
    }

    void addHandler(ConnectionHandler connHandler) {
        connectionHandlers.put(connHandler.getName(), connHandler);
        try {
            connHandler.sendPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
        } catch (IOException e) {
            logger.error("При при отправке клиенту " + connHandler.getName() + " списка комнат произошла ошиба", e.toString());
        }
    }

    /**
     *
     * @param nickname
     * @return
     */
    Boolean isAlreadyConnected(String nickname) {
        return connectionHandlers.containsKey(nickname);
    }

    void newChatRoom(String name) {
        if (!name.isEmpty() && !name.startsWith("@") && !name.startsWith(">>")) {
            rooms.put(name, ConcurrentHashMap.newKeySet());
            broadcastPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
        }
    }

    void removeChatRoom(String name) {
        if (!MAIN_CHATROOM_NAME.equals(name)) {
            rooms.remove(name);
            broadcastPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
        }
    }

    void addToChatRoom(ConnectionHandler connHandler, String roomName) {
        if (rooms.get(roomName) != null) {
            rooms.get(roomName).add(connHandler);
            broadcastPacket(new Packet(PacketType.USERS_LIST, new UsersList(roomName, getUsersList(roomName))), roomName);
            sendMessage(null, new Message(roomName, connHandler.getName(), null, "вошёл в комнату"));
        }
    }

    void removeFromChatRoom(ConnectionHandler connHandler, String roomName) {
        if (rooms.get(roomName) != null) {
            if (rooms.get(roomName).remove(connHandler)) {
                broadcastPacket(new Packet(PacketType.USERS_LIST, new UsersList(roomName, getUsersList(roomName))), roomName);
                sendMessage(null, new Message(roomName, connHandler.getName(), null, "покинул комнату"));
            }
        }
    }

    void broadcastPacket(Packet packet) {
        for (ConnectionHandler connHandler : connectionHandlers.values()) {
            try {
                connHandler.sendPacket(packet);
            } catch (IOException e) {
                logger.error("При отправке пакета клиенту " + connHandler.getName() + " произошла ошибка", e);
            }
        }
    }

    void broadcastPacket(Packet packet, String roomName) {
        for (ConnectionHandler connHandler : rooms.get(roomName)) {
            try {
                connHandler.sendPacket(packet);
            } catch (IOException e) {
                logger.error("При отправке пакета клиенту " + connHandler.getName() + "произошла ошибка", e);
            }
        }
    }

    List<String> getUsersList(String chatRoom) {
        return rooms.get(chatRoom).stream().map(Thread::getName).collect(Collectors.toList());
    }
}


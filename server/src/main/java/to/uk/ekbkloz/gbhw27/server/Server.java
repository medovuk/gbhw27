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
                logger.error(e.getMessage());
            }
        }
        if (isInterrupted()) {
            logger.info(getClass().getSimpleName() + " was interrupted");
            try{
                serverSocket.close();
            }
            catch(IOException e){
                logger.error(e.getMessage());
            }
        }
    }

    void sendMessage(Message message) {
        Packet packet = new Packet(PacketType.MESSAGE, message);
        if (message.getTo() != null && !message.getTo().isEmpty()) {
            try {
                connectionHandlers.get(message.getTo()).sendPacket(packet);
            } catch (IOException e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
        else if (message.getRoom() != null && !message.getRoom().isEmpty() && !MAIN_CHATROOM_NAME.equals(message.getRoom())) {
            broadcastPacket(packet, message.getRoom());
        }
        else {
            broadcastPacket(packet);
        }
    }

    void removeHandler(ConnectionHandler connHandler) {
        connectionHandlers.remove(connHandler.getName());
        for (String roomName : rooms.keySet()) {
            removeFromChatRoom(connHandler, roomName);
        }
    }

    void addHandler(ConnectionHandler connHandler) {
        connectionHandlers.put(connHandler.getName(), connHandler);
        addToChatRoom(connHandler, MAIN_CHATROOM_NAME);
        try {
            connHandler.sendPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
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
        rooms.put(name, ConcurrentHashMap.newKeySet());
        broadcastPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
    }

    void removeChatRoom(String name) {
        rooms.remove(name);
        broadcastPacket(new Packet(PacketType.ROOMS_LIST, new RoomsList(rooms.keySet())));
    }

    void addToChatRoom(ConnectionHandler connHandler, String roomName) {
        rooms.get(roomName).add(connHandler);
        broadcastPacket(new Packet(PacketType.USERS_LIST, new UsersList(roomName, getUsersList(roomName))), roomName);
        sendMessage(new Message(roomName, connHandler.getName(), null, "вошёл в комнату"));
    }

    void removeFromChatRoom(ConnectionHandler connHandler, String roomName) {
        if (rooms.get(roomName).remove(connHandler)) {
            broadcastPacket(new Packet(PacketType.USERS_LIST, new UsersList(roomName, getUsersList(roomName))), roomName);
            sendMessage(new Message(roomName, connHandler.getName(), null, "покинул комнату"));
        }
    }

    void broadcastPacket(Packet packet) {
        for (ConnectionHandler connHandler : connectionHandlers.values()) {
            try {
                connHandler.sendPacket(packet);
            } catch (IOException e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    void broadcastPacket(Packet packet, String roomName) {
        for (ConnectionHandler connHandler : rooms.get(roomName)) {
            try {
                connHandler.sendPacket(packet);
            } catch (IOException e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    List<String> getUsersList(String chatRoom) {
        return rooms.get(chatRoom).stream().map(Thread::getName).collect(Collectors.toList());
    }
}


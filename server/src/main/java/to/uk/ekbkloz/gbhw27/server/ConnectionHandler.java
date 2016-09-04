package to.uk.ekbkloz.gbhw27.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import to.uk.ekbkloz.gbhw27.proto.*;
import to.uk.ekbkloz.gbhw27.proto.exceptions.AuthException;

/**
 * Created by Andrey on 04.09.2016.
 */
public class ConnectionHandler extends Thread {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static AtomicLong sessionCounter = new AtomicLong();
    private final Socket clientSocket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Server server;
    private Boolean authenticated;

    public ConnectionHandler(Socket clientSocket, Server server) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        setName("Client #" + sessionCounter.incrementAndGet());
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        logger.info(getName() + " connected");
        authenticated = false;
        //server.broadcastMessage(getName() + " вошёл в чат.");
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            if (!authenticated) {
                try {
                    Packet packet = (Packet) input.readObject();
                    Credential credential = null;
                    if (PacketType.AUTH_REQUEST.equals(packet.getType())) {
                        credential = packet.getPayload(Credential.class);
                    }
                    String nickname = SQLHandler.getNickByCredential(credential.getLogin(), credential.getPassword());
                    if (nickname != null && !nickname.isEmpty() && !server.isAlreadyConnected(nickname)) {
                        sendPacket(new Packet(PacketType.AUTH_RESPONSE, new AuthResponse(true, nickname, Server.MAIN_CHATROOM_NAME, null)));

                        authenticated = true;
                        logger.info("Succesfully authenticated as " + nickname);
                        setName(nickname);
                        server.addHandler(this);
                    }
                    else {
                        logger.info("Login failed!");
                        sendPacket(new Packet(PacketType.AUTH_RESPONSE, new AuthResponse(false, null, null, new AuthException("Login failed!"))));
                        interrupt();
                    }
                } catch (IOException | SQLException | ClassNotFoundException e) {
                    logger.error(e.getMessage());
                    logger.info("Login failed!");
                    try {
                        sendPacket(new Packet(PacketType.AUTH_RESPONSE, new AuthResponse(false, null, null, new AuthException("Login failed!"))));
                    } catch (IOException e1) {
                        logger.error(e1.getMessage());
                    }
                    interrupt();
                }
            }
            else {
                try {
                    Packet packet = (Packet) input.readObject();
                    switch (packet.getType()) {
                        case AUTH_REQUEST:
                            break;
                        case AUTH_RESPONSE:
                            break;
                        case USERS_LIST:
                            break;
                        case MESSAGE:
                            Message message = packet.getPayload(Message.class);
                            message.setFrom(getName());
                            server.sendMessage(message);
                            break;
                        case CREATE_CHATROOM:
                            server.newChatRoom(packet.getPayload(CreateRoom.class).getRoomName());
                            break;
                        case JOIN_CHATROOM:
                            server.addToChatRoom(this, packet.getPayload(JoinRoom.class).getRoomName());
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    interrupt();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
                interrupt();
            }
        }
        if(isInterrupted()) {
            try{
                clientSocket.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage());
            }
            if (authenticated) {
                server.removeHandler(this);
            }
            logger.info(getName() + " disconnected");
        }
    }

    void sendPacket(Packet packet) throws IOException {
        try {
            output.writeObject(packet);
            output.flush();
        } catch (IOException e) {
            this.interrupt();
            throw e;
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getName().equals(obj);
    }
}


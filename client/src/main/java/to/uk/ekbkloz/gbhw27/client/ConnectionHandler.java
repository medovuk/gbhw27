package to.uk.ekbkloz.gbhw27.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to.uk.ekbkloz.gbhw27.proto.AuthResponse;
import to.uk.ekbkloz.gbhw27.proto.Credential;
import to.uk.ekbkloz.gbhw27.proto.Packet;
import to.uk.ekbkloz.gbhw27.proto.PacketType;
import to.uk.ekbkloz.gbhw27.proto.exceptions.AuthException;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Andrey on 04.09.2016.
 */
public class ConnectionHandler implements Closeable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String SERVER_HOSTNAME;
    private final Integer SERVER_PORT;
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
     *
     */
    private boolean authenticated = false;
    private boolean closed = false;

    public ConnectionHandler(String hostname, Integer port) {
        SERVER_HOSTNAME = hostname;
        SERVER_PORT = port;
    }

    public AuthResponse authenticate(Credential credential) throws IOException, ClassNotFoundException, AuthException {
        closed = false;
        clientSocket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        try {
            sendPacket(new Packet(PacketType.AUTH_REQUEST, credential));
            Packet packet = receivePacket();
            if (PacketType.AUTH_RESPONSE.equals(packet.getType())) {
                AuthResponse authResponse = packet.getPayload(AuthResponse.class);
                if (!authResponse.isSucceeded()) {
                    close();
                    throw authResponse.getException();
                }
                else {
                    authenticated = true;
                    return authResponse;
                }
            }
            else {
                close();
                throw new AuthException("пришёл неверный ответ от сервера!");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.toString());
            e.printStackTrace();
            close();
            throw e;
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        authenticated = false;
        if (output != null) output.close();
        if (input != null) input.close();
        if (clientSocket != null) clientSocket.close();
        closed = true;
    }

    public void sendPacket(Packet packet) throws IOException {
        try {
            output.writeObject(packet);
            output.flush();
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public Packet receivePacket() throws IOException, ClassNotFoundException {
        try {
            return (Packet) input.readObject();
        } catch (IOException e) {
            close();
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }
}

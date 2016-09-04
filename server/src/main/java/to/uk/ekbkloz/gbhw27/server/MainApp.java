package to.uk.ekbkloz.gbhw27.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Andrey on 03.09.2016.
 */
public class MainApp {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(MainApp.class);
        try{
            Server server = new Server(8189);
            server.start();
            try {
                server.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        catch (IOException | ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage());
        }
    }
}

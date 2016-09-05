package to.uk.ekbkloz.gbhw27.server;

import java.sql.*;

/**
 * Created by Andrey on 04.09.2016.
 */
public class SQLHandler {
    private static Connection conn = null;

    /**
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:user_db.db");
    }

    /**
     *
     * @throws SQLException
     */
    public static void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     *
     * @param login
     * @param password
     * @param nickname
     * @throws SQLException
     */
    public static void insert(String login, String password, String nickname) throws SQLException {
        PreparedStatement prepStmnt = conn.prepareStatement("INSERT INTO users (Login, Password, Nickname) VALUES (?, ?, ?)");
        prepStmnt.setString(1, login);
        prepStmnt.setString(2, password);
        prepStmnt.setString(3, nickname);
        prepStmnt.execute();
    }

    /**
     *
     * @param login
     * @param password
     * @return
     * @throws SQLException
     */
    public static String getNickByCredential(String login, String password) throws SQLException {
        ResultSet resultSet;
        String nickname = null;
        PreparedStatement prepStmnt = conn.prepareStatement("SELECT Nickname FROM users WHERE Login = ? AND Password = ?");
        prepStmnt.setString(1, login);
        prepStmnt.setString(2, password);
        resultSet = prepStmnt.executeQuery();
        while (resultSet.next()) {
            nickname = resultSet.getString(1);
        }
        return nickname;
    }
}

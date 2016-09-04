package to.uk.ekbkloz.gbhw27.client.ui;

import to.uk.ekbkloz.gbhw27.client.MainWindow;
import to.uk.ekbkloz.gbhw27.proto.AuthResponse;
import to.uk.ekbkloz.gbhw27.proto.Credential;
import to.uk.ekbkloz.gbhw27.proto.exceptions.AuthException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Andrey on 04.09.2016.
 */
public class AuthPanel implements UIComponent {
    private final MainWindow mainWindow;
    private final JPanel authPanel;
    /**
     * Поле для ввода логина
     */
    private final JTextField loginField;
    /**
     * Поле для ввода пароля
     */
    private final JPasswordField passwordField;
    /**
     * Кнопка для подтверждения аутентификации
     */
    private final JButton authButton;
    private final ActionListener action;

    public AuthPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AuthResponse authResponse = mainWindow.getConnectionHandler().authenticate(new Credential(loginField.getText(), new String(passwordField.getPassword())));
                    mainWindow.setAuthenticated(authResponse.isSucceeded());
                    mainWindow.addRoomTab(authResponse.getRoomName(), false, null);
                    mainWindow.setNickname(authResponse.getNickname());
                }
                catch(IOException | AuthException | ClassNotFoundException e1) {
                    JOptionPane.showMessageDialog(authPanel, "Error: " + e1.getMessage());
                }
            }
        };
        authPanel = new JPanel();
        authPanel.setLayout(new GridLayout(1, 3));
        loginField = new JTextFieldWithHint(null, "логин");
        loginField.addActionListener(action);
        authPanel.add(loginField);
        passwordField = new JPasswordFieldWithHint(null, "пароль");
        passwordField.addActionListener(action);
        authPanel.add(passwordField);
        authButton = new JButton("Вход");
        authButton.addActionListener(action);
        authPanel.add(authButton);
    }

    @Override
    public Component getRootComponent() {
        return authPanel;
    }

    public void grabFocus() {
        loginField.grabFocus();
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    @Override
    public void setEnabled(boolean enabled) {
        authPanel.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible) {
        authPanel.setVisible(visible);
    }
}

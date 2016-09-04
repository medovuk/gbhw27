package to.uk.ekbkloz.gbhw27.client.ui;

import to.uk.ekbkloz.gbhw27.client.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Andrey on 04.09.2016.
 */
public class UserInputPanel {
    private final MainWindow mainWindow;
    /**
     * панель для ввода сообщений
     */
    private final JPanel userInputPanel;
    /**
     * текстовое поле для ввода
     */
    private final JTextField userInputText;

    /**
     * кнопка для ввода
     */
    private final JButton userInputButton;
    private final ActionListener action;

    public UserInputPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        action = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                mainWindow.processUserInput();
            }
        };
        userInputPanel = new JPanel();
        userInputPanel.setLayout(new BorderLayout());
        userInputText = new JTextFieldWithHint(null, "Введите сообщение");
        userInputText.addActionListener(action);
        userInputPanel.add(userInputText, BorderLayout.CENTER);
        userInputButton = new JButton("Ввод");
        userInputButton.addActionListener(action);
        userInputPanel.add(userInputButton, BorderLayout.EAST);
    }

    public JPanel getRootComponent() {
        return userInputPanel;
    }

    public void grabFocus() {
        userInputText.grabFocus();
    }

    public String getText() {
        return userInputText.getText();
    }

    public void setText(String text) {
        userInputText.setText(text);
    }
}

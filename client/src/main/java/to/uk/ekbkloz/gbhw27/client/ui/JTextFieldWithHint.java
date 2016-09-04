package to.uk.ekbkloz.gbhw27.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrey on 04.09.2016.
 */
public class JTextFieldWithHint extends JTextField {
    private String hint;

    public JTextFieldWithHint(String text, String hint) {
        super(text);
        this.hint = hint;
    }

    public JTextFieldWithHint() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(hint, 4, 16);
        }
    }
}

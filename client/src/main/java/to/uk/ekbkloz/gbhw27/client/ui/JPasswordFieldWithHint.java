package to.uk.ekbkloz.gbhw27.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrey on 04.09.2016.
 */
public class JPasswordFieldWithHint extends JPasswordField {
    private String hint;

    public JPasswordFieldWithHint(String text, String hint) {
        super(text);
        this.hint = hint;
    }

    public JPasswordFieldWithHint() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getPassword().length == 0) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(hint, 4, 16);
        }
    }
}

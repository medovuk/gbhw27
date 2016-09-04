package to.uk.ekbkloz.gbhw27.client.ui;

import java.awt.*;

/**
 * Created by Andrey on 04.09.2016.
 */
public interface UIComponent {
    public Component getRootComponent();
    public void setEnabled(boolean enabled);
    public void setVisible(boolean visible);
}

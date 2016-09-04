package to.uk.ekbkloz.gbhw27.client;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Andrey on 03.09.2016.
 */
public class MainApp {
    public static void main(String[] args) {
        try {
            new MainWindow();
        }
        catch (HeadlessException | IOException e) {
            e.printStackTrace();
        }
    }
}

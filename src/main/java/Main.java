import com.formdev.flatlaf.FlatDarkLaf;
import src.NTListener;
import src.ui.UI;

public class Main {
    public static void main(final String[] args) {
        FlatDarkLaf.setup();
        new UI(new NTListener());
    }
}

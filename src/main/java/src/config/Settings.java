package src.config;

import java.net.URL;
import java.util.Objects;

public class Settings {
    public static final String HOSTNAME = "localhost";
    public static final String CLIENT_NAME = "TitanDash";

    public static final String AUTO_NETWORK_TABLE = "AutoSelector";
    public static final String AUTO_SUBSCRIBER_TOPIC = "AutoOptions";
    public static final String AUTO_PUBLISH_TOPIC = "SelectedAuto";

    public static final String PROFILE_NETWORK_TABLE = "ProfileSelector";
    public static final String PROFILE_PUBLISH_TOPIC = "SelectedProfile";
    public static final String PROFILE_SUBSCRIBER_TOPIC = "ProfileOptions";

    public static final String ICON_PATH = "/titandashicon.png";

    public static URL getResource(final String path) {
        return Objects.requireNonNull(Settings.class.getResource(path));
    }
}

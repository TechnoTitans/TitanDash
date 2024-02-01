package src;

import edu.wpi.first.networktables.*;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import src.config.Settings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NTListener implements AutoCloseable {
    static {
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);

        try {
            CombinedRuntimeLoader.loadLibraries(
                    NTListener.class,
                    "wpiutiljni",
                    "ntcorejni"
            );
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private final NetworkTableInstance networkTableInstance;

    private final StringArraySubscriber autoSubscriber;
    private final StringPublisher autoPublisher;

    private final StringArraySubscriber profileSubscriber;
    private final StringPublisher profilePublisher;

    public NTListener() {
        this.networkTableInstance = NetworkTableInstance.getDefault();
        this.networkTableInstance.startClient4(Settings.CLIENT_NAME);
        this.networkTableInstance.setServer(Settings.HOSTNAME); // where TEAM=190, 294, etc, or use inst.setServer("hostname") or similar
        this.networkTableInstance.startDSClient();

        final NetworkTable ntAutoTable = this.networkTableInstance.getTable(Settings.AUTO_NETWORK_TABLE);
        this.autoSubscriber = ntAutoTable.getStringArrayTopic(Settings.AUTO_SUBSCRIBER_TOPIC)
                .subscribe(new String[0]);

        this.autoPublisher = ntAutoTable.getStringTopic(Settings.AUTO_PUBLISH_TOPIC)
                .publish();


        final NetworkTable ntProfileTable = this.networkTableInstance.getTable(Settings.PROFILE_NETWORK_TABLE);
        this.profileSubscriber = ntProfileTable.getStringArrayTopic(Settings.PROFILE_SUBSCRIBER_TOPIC)
                .subscribe(new String[0]);

        this.profilePublisher = ntProfileTable.getStringTopic(Settings.PROFILE_PUBLISH_TOPIC)
                .publish();
    }

    public NetworkTableInstance getNetworkTableInstance() {
        return networkTableInstance;
    }

    public StringArraySubscriber getAutoSubscriber() {
        return autoSubscriber;
    }

    public StringArraySubscriber getProfileSubscriber() {
        return profileSubscriber;
    }

    public List<String> getAutoPaths() {
        return Collections.unmodifiableList(Arrays.asList(autoSubscriber.get()));
    }

    public void selectAuto(final String autoPath) {
        autoPublisher.set(autoPath);
    }

    public List<String> getDriverProfiles() {
        return Collections.unmodifiableList(Arrays.asList(profileSubscriber.get()));
    }

    public void selectProfile(final String profile) {
        profilePublisher.set(profile);
    }

    @Override
    public void close() {
        autoSubscriber.close();
        autoPublisher.close();

        profileSubscriber.close();
        profilePublisher.close();
    }
}

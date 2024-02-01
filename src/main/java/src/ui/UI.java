package src.ui;

import edu.wpi.first.networktables.NetworkTableEvent;
import src.NTListener;
import src.config.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class UI extends JFrame {
    private final NTListener ntListener;

    private JComboBox<String> autoSelector;
    private JLabel autonomousLabel;
    private JMenu fileMenu;
    private JMenuBar menuBar;
    private JLabel profileLabel;
    private JComboBox<String> profileSelector;
    private JMenuItem refreshMenuItem;

    public UI(final NTListener ntListener) {
        this.ntListener = ntListener;

        EventQueue.invokeLater(() -> {
            initComponents();
            setVisible(true);
            ntEventListeners();
        });
    }

    private void ntEventListeners() {
        ntListener.getNetworkTableInstance().addListener(
                ntListener.getAutoSubscriber(),
                EnumSet.of(NetworkTableEvent.Kind.kValueAll),
                (event) -> populateAutonomousPaths()
        );

        ntListener.getNetworkTableInstance().addListener(
                ntListener.getProfileSubscriber(),
                EnumSet.of(NetworkTableEvent.Kind.kValueAll),
                (event) -> populateDriverProfiles()
        );

        ntListener.getNetworkTableInstance().addConnectionListener(
                true,
                (event) -> {
                    if (event.is(NetworkTableEvent.Kind.kConnected)) {
                        setTitle("TitanDash | Connected");
                    } else if (event.is(NetworkTableEvent.Kind.kDisconnected)) {
                        setTitle("TitanDash | Disconnected");
                    } else {
                        setTitle("TitanDash | Unknown");
                    }
                    refreshMenuItemActionPerformed();
                }
        );
    }

    private void initComponents() {
        autoSelector = new JComboBox<>();
        profileSelector = new JComboBox<>();
        autonomousLabel = new JLabel();
        profileLabel = new JLabel();
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        refreshMenuItem = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TitanDash | Disconnected");
        setIconImage(new ImageIcon(Settings.getResource(Settings.ICON_PATH)).getImage());
        setMinimumSize(new Dimension(800, 225));
        setPreferredSize(new Dimension(800, 225));
        setLocationRelativeTo(null);

        Arrays.stream(autoSelector.getKeyListeners()).forEach(autoSelector::removeKeyListener);
        Arrays.stream(profileSelector.getKeyListeners()).forEach(profileSelector::removeKeyListener);

        autoSelector.addActionListener(this::autoSelectorActionPerformed);
        profileSelector.addActionListener(this::profileSelectorActionPerformed);

        autonomousLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autonomousLabel.setHorizontalAlignment(SwingConstants.CENTER);
        autonomousLabel.setText("Autonomous");

        profileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileLabel.setText("Profile Selector");

        fileMenu.setText("File");

        refreshMenuItem.setText("Reset");
        refreshMenuItem.addActionListener(evt -> refreshMenuItemActionPerformed());
        fileMenu.add(refreshMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(106, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING, false
                                        )
                                        .addComponent(
                                                autoSelector,
                                                GroupLayout.PREFERRED_SIZE,
                                                287,
                                                GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                autonomousLabel,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE
                                        ))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(
                                                profileLabel,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE
                                        )
                                        .addComponent(
                                                profileSelector,
                                                GroupLayout.PREFERRED_SIZE,
                                                287,
                                                GroupLayout.PREFERRED_SIZE
                                        ))
                                .addContainerGap(107, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(
                                                autonomousLabel,
                                                GroupLayout.PREFERRED_SIZE,
                                                27,
                                                GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                profileLabel,
                                                GroupLayout.PREFERRED_SIZE,
                                                27,
                                                GroupLayout.PREFERRED_SIZE
                                        ))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(
                                                autoSelector,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                profileSelector,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE
                                        ))
                                .addContainerGap(116, Short.MAX_VALUE))
        );

        pack();

        refreshMenuItemActionPerformed();
    }

    private void refreshMenuItemActionPerformed() {
        populateAutonomousPaths();
        populateDriverProfiles();
    }

    private void autoSelectorActionPerformed(final ActionEvent evt) {
        final String selectedAuto = (String) autoSelector.getSelectedItem();
        if (selectedAuto != null) {
            ntListener.selectAuto(selectedAuto);
        }
    }

    private void profileSelectorActionPerformed(final ActionEvent evt) {
        final String selectedProfile = (String) profileSelector.getSelectedItem();
        if (selectedProfile != null) {
            ntListener.selectProfile(selectedProfile);
        }
    }

    private void populateAutonomousPaths() {
        autoSelector.removeAllItems();
        final List<String> autoPaths = ntListener.getAutoPaths();
        autoPaths.forEach(autoSelector::addItem);
        if (!autoPaths.isEmpty()) {
            ntListener.selectAuto(autoPaths.get(0));
        }
    }

    private void populateDriverProfiles() {
        profileSelector.removeAllItems();
        final List<String> driverProfiles = ntListener.getDriverProfiles();
        driverProfiles.forEach(profileSelector::addItem);
        if (!driverProfiles.isEmpty()) {
            ntListener.selectProfile(driverProfiles.get(0));
        }
    }
}

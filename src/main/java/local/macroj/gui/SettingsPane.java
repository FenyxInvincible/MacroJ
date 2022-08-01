package local.macroj.gui;

import local.macroj.data.RuntimeConfig;
import local.macroj.service.GuiService;
import local.macroj.service.MemoryLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Optional;

@Component
@Slf4j
public class SettingsPane extends JPanel {
	private JComboBox logLevel;
	private JTextField defaultEditor;
	private JTextField maxLogs;

	@Autowired
	private RuntimeConfig config;
	@Autowired
	private MemoryLogging memoryLogging;

	public SettingsPane() {

		JLabel lblNewLabel = new JLabel("Editor");
		
		JLabel lblNewLabel_1 = new JLabel("Max logs");
		
		defaultEditor = new JTextField();
		defaultEditor.setColumns(10);
		
		maxLogs = new JTextField();
		maxLogs.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Log level");
		
		logLevel = new JComboBox();
		logLevel.setModel(new DefaultComboBoxModel(new String[] {"INFO", "DEBUG", "TRACE"}));
		logLevel.setSelectedIndex(0);
		
		JButton filePicker = new JButton("Browse...");
		filePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int retVal = fc.showOpenDialog(SettingsPane.this);
				if(retVal == JFileChooser.APPROVE_OPTION) {
					defaultEditor.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}			
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_2))
					.addGap(38)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(logLevel, 0, 230, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(defaultEditor, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(filePicker))
						.addComponent(maxLogs, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(defaultEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(filePicker))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(maxLogs, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(logLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2))
					.addGap(210))
		);
		setLayout(groupLayout);
	}

	@PostConstruct
	public void refresh() {
		maxLogs.setText(String.valueOf(config.getSettings().getMaxLogLines()));
		defaultEditor.setText(config.getSettings().getDefaultEditor());

		for (int i = 0; i < logLevel.getItemCount(); i++) {
			if (logLevel.getItemAt(i).toString().equals(config.getSettings().getLogLevel())) {
				logLevel.setSelectedIndex(i);
			}
		}
	}

	public void save() {
		RuntimeConfig.Settings settings = config.getSettings();

		settings.setLogLevel(Optional.ofNullable((String)logLevel.getSelectedItem()).orElse("INFO"));

		try {
			settings.setMaxLogLines(Integer.parseInt(maxLogs.getText()));
		} catch (NumberFormatException e) {
			log.error("Incorrect number for max logs");
		}

		settings.setDefaultEditor(defaultEditor.getText());

		config.saveSettings();
		memoryLogging.resetLogger();

		log.debug("Save settings");
	}
}

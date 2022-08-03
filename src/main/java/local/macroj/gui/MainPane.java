package local.macroj.gui;

import local.macroj.service.GuiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@Component
@Slf4j
public class MainPane extends JPanel {

	private final JLabel mainLabel;
	private final JCheckBox logsCheckBox;
	private final JScrollPane logsPane;
	private final JButton copyButton;
	private final JButton loadButton;
	private final JButton clearButton;
	@Autowired
	private GuiService guiService;

	private final JList profilesList;

	private final JTextArea logsField;

	@Value("${app.profilesPath}")
	private String profilesPath;

	public MainPane() {
		
		loadButton = new JButton("Load");
		loadButton.setBackground(new Color(166, 247, 180));
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleProfile();
			}
		});
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setBackground(new Color(192, 192, 192));
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editProfile();
			}
		});
		
		logsCheckBox = new JCheckBox("Show logs");
		logsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleLogs();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Profiles:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		mainLabel = new JLabel("Select profile");
		
		logsPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
						.addComponent(logsPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(logsCheckBox, Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(mainLabel, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnEdit)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(loadButton))
						.addComponent(lblNewLabel, Alignment.TRAILING))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(loadButton)
						.addComponent(btnEdit)
						.addComponent(mainLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(logsCheckBox)
					.addGap(4)
					.addComponent(logsPane, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
					.addContainerGap())
		);

		logsField = new JTextArea();
		logsField.setFont(new Font("Monospaced", Font.PLAIN, 11));
		logsPane.setViewportView(logsField);
		logsField.setEditable(false);
		
		JPanel panel = new JPanel();
		logsPane.setColumnHeaderView(panel);
		
		
				copyButton = new JButton("Copy");
				panel.add(copyButton);
				copyButton.setBackground(new Color(232, 230, 153));
				
				clearButton = new JButton("Clear");
				clearButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						guiService.clearLogs();
					}
				});
				clearButton.setBackground(new Color(128, 0, 255));
				panel.add(clearButton);
				copyButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(new StringSelection(logsField.getText()), null);
					}
				});
				copyButton.setVisible(false);
				clearButton.setVisible(false);

		profilesList = new JList();
		profilesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateLoadButton();
			}
		});
		scrollPane.setViewportView(profilesList);
		profilesList.setSize(new Dimension(0, 60));
		profilesList.setVisibleRowCount(3);
		profilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		profilesList.setModel(new AbstractListModel() {
			String[] values = new String[] {};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		profilesList.setSelectedIndex(0);
		setLayout(groupLayout);
		logsField.setVisible(false);
	}

	private void toggleLogs() {
		boolean state = logsCheckBox.isSelected();
		if (state) {
			guiService.enableLogs(logsField);
			logsField.setVisible(true);
			copyButton.setVisible(true);
			clearButton.setVisible(true);
		} else {
			guiService.disableLogs(logsField);
			logsField.setVisible(false);
			copyButton.setVisible(false);
			clearButton.setVisible(false);
		}
	}

	private void editProfile() {
		if(profilesList.getSelectedValue() != null) {
			guiService.editProfile(currentListElementRealName());
		}
	}

	private void toggleProfile() {
		if (profilesList.getSelectedValue() != null
				&& guiService.toggleProfile(currentListElementRealName())) {
			updateLoadButton();
		} else {
			log.warn("Could not load or stop profile");
		}
	}

	private void updateLoadButton() {
		if(guiService.getCurrentProfileName() != null) {
			mainLabel.setText(String.format("Current profile: %s", guiService.getCurrentProfileName()));

			String currentProfileName = guiService.getCurrentProfileName();

			if(currentProfileName != null && currentProfileName.equals(currentListElementRealName())) {
				loadButton.setText("Stop");
			} else {
				loadButton.setText("Load");
			}
		} else {
			mainLabel.setText(String.format("Select profile..."));
			loadButton.setText("Load");
		}
	}

	private String currentListElementRealName() {
		return profilesList.getSelectedValue().toString()
				.replace("[JSON]", ".json")
				.replace("[YAML]", ".yaml");
	}

	@PostConstruct
	public void init() {
		profilesPath = "./profiles";
		String[] profilesArray = listProfilesForFolder(new File(profilesPath)).toArray(new String[0]);
		addToList(profilesList, profilesArray);
	}

	private void addToList(JList profilesList, String[] profilesArray) {
		DefaultListModel listModel = new DefaultListModel();
		Arrays.stream(profilesArray).forEach(listModel::addElement);
		profilesList.setModel(listModel);
	}

	private List<String> listProfilesForFolder(final File folder) {
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return Arrays.stream(folder.listFiles()).sorted().
				filter(f -> !f.isDirectory() && f.getName().startsWith("mapping-"))
				.filter(f -> f.getName().endsWith(".json") || f.getName().endsWith(".yaml"))
				.map(f -> f.getName().replace("mapping-", "")
						.replace(".json", "[JSON]")
						.replace(".yaml", "[YAML]")
				)
				.collect(Collectors.toList());
	}

	public void refresh() {
		init();
	}
}

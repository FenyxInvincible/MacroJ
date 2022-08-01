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

@Component
@Slf4j
public class MainPane extends JPanel {

	private final JLabel mainLabel;
	private final JCheckBox logsCheckBox;
	private final JScrollPane logsPane;
	private final JButton copyButton;
	@Autowired
	private GuiService guiService;

	private final JList profilesList;

	private final JTextArea logsField;

	@Value("${app.profilesPath}")
	private String profilesPath;

	public MainPane() {
		
		JButton loadButton = new JButton("Load");
		loadButton.setBackground(new Color(166, 247, 180));
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadProfile();
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
						.addComponent(logsPane, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
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
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(loadButton)
						.addComponent(btnEdit)
						.addComponent(mainLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(logsCheckBox)
					.addGap(4)
					.addComponent(logsPane, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
					.addContainerGap())
		);

		logsField = new JTextArea();
		logsPane.setViewportView(logsField);
		logsField.setEditable(false);


		copyButton = new JButton("Copy");
		copyButton.setBackground(new Color(232, 230, 153));
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(logsField.getText()), null);
			}
		});
		logsPane.setColumnHeaderView(copyButton);

		profilesList = new JList();
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
		copyButton.setVisible(false);
		logsField.setVisible(false);
	}

	private void toggleLogs() {
		boolean state = logsCheckBox.isSelected();
		if (state) {
			guiService.enableLogs(logsField);
			logsField.setVisible(true);
			copyButton.setVisible(true);
		} else {
			guiService.disableLogs(logsField);
			logsField.setVisible(false);
			copyButton.setVisible(false);
		}
	}

	private void editProfile() {
		if(profilesList.getSelectedValue() != null) {
			guiService.editProfile(profilesList.getSelectedValue().toString());
		}
	}

	private void loadProfile() {
		if (profilesList.getSelectedValue() != null) {
			boolean res = guiService.loadProfile(profilesList.getSelectedValue().toString());
			if (res) {
				mainLabel.setText(String.format("Current profile: %s", guiService.getCurrentProfileName()));
			}
		} else {
			JOptionPane.showMessageDialog(this, "You need to select profile.");
		}
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
				.map(f -> f.getName().replace("mapping-", "")
						.replace(".json", ""))
				.collect(Collectors.toList());
	}

	public void refresh() {
		init();
	}
}

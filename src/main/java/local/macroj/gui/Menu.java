package local.macroj.gui;

import local.macroj.service.GuiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
public class Menu extends JMenuBar {

	@Autowired
	private GuiService guiService;

	@Autowired
	private SettingsPane settingsPane;

	public Menu() {
		
		JMenu mainMenu = new JMenu("File");
		add(mainMenu);
		
		JMenuItem createProfileItem = new JMenuItem("Create profile...");
		createProfileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProfileTrigger();
			}
		});
		mainMenu.add(createProfileItem);
		
		JMenuItem SettingsItem = new JMenuItem("Settings");
		SettingsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectEditor();
			}
		});
		mainMenu.add(SettingsItem);
		
		JMenu helpItem = new JMenu("Help");
		add(helpItem);
		
		JMenuItem keysMenuItem = new JMenuItem("Keys list");
		keysMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/FenyxInvincible/MacroJ/blob/master/src/main/resources/keys.json"));
				} catch (IOException | URISyntaxException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		helpItem.add(keysMenuItem);
		
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/FenyxInvincible/MacroJ/blob/master/README.md"));
				} catch (IOException | URISyntaxException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		helpItem.add(aboutMenuItem);
	}

	private void selectEditor() {
		settingsPane.refresh();
		JOptionPane.showMessageDialog(this, settingsPane);
		settingsPane.save();
	}

	private void createProfileTrigger() {
		String name = JOptionPane.showInputDialog(null,
				"Select profile name.", null);
		if(name != null) {
			GuiService.Result res = guiService.addProfile(name);
			JOptionPane.showMessageDialog(null,
					res.getMessage(), "Profile creation", res.isSuccessful() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
		}
	}
}

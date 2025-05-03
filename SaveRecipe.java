package gui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;

public class SaveRecipe extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTextArea sourceArea;
	private final JTextField nameField;

	public SaveRecipe(JTextArea sourceArea) {
		super("Сохранить рецепт");
		this.sourceArea = sourceArea;

		setLayout(new FlowLayout());
		nameField  = new JTextField(15);
		JButton save = new JButton("Сохранить рецепт");
		save.addActionListener(e -> saveFile());

		add(new JLabel("Введите название рецепта:"));
		add(nameField);
		add(save);

		setSize(400, 100);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void saveFile() {
		String filename = nameField.getText().toLowerCase().replace(" ", "_") + ".txt";
		try (PrintWriter pw = new PrintWriter(filename, "UTF-8")) {
			pw.print(sourceArea.getText());
			dispose();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(
					this,
					"Ошибка при сохранении файла.",
					"Ошибка",
					JOptionPane.ERROR_MESSAGE
			);
		}

		




	}
}

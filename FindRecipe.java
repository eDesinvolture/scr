package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FindRecipe extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTextArea targetArea;
	private final JTextField nameField;

	public FindRecipe(JTextArea targetArea) {
		super("Найти рецепт");
		this.targetArea = targetArea;

		setLayout(new FlowLayout());
		nameField  = new JTextField(15);
		JButton find = new JButton("Найти рецепт");
		find.addActionListener(e -> findFile());

		add(new JLabel("Название рецепта:"));
		add(nameField);
		add(find);

		setSize(400, 100);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void findFile() {
		String filename = nameField.getText().toLowerCase().replace(" ", "_") + ".txt";
		File file = new File(filename);
		try (Scanner sc = new Scanner(file)) {
			StringBuilder sb = new StringBuilder();
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine()).append("\n");
			}
			targetArea.setText(sb.toString());
		} catch (FileNotFoundException e) {
			targetArea.setText("Рецепт не найден.");
		} finally {
			dispose();
		}
	}
}

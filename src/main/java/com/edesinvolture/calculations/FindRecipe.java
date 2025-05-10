package com.edesinvolture.calculations;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
		File file = new File("recipes", filename); // ищем в ./recipes/название.txt

		if (!file.exists()) {
			targetArea.setText("Рецепт не найден.");
			dispose();
			return;
		}

		try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
			StringBuilder sb = new StringBuilder();
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine()).append("\n");
			}
			targetArea.setText(sb.toString());
		} catch (IOException e) {
			targetArea.setText("Ошибка при чтении файла.");
		}
		dispose();
	}

}

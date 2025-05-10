package com.edesinvolture.calculations;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class FoodCalculator extends JPanel {
	static class Food {
		String name;
		double calPer100, protPer100, fatPer100, carbPer100;
		Food(String name, double c, double p, double f, double h) {
			this.name       = name;
			this.calPer100  = c;
			this.protPer100 = p;
			this.fatPer100  = f;
			this.carbPer100 = h;
		}
		double cpg() { return calPer100  / 100.0; }
		double ppg() { return protPer100 / 100.0; }
		double fpg() { return fatPer100  / 100.0; }
		double kpg() { return carbPer100 / 100.0; }
	}

	private final ArrayList<Food>       foodlist = new ArrayList<>();
	private final ArrayList<JTextField> fields   = new ArrayList<>();
	private final JButton               calcBtn, saveBtn, findBtn, clearBtn;
	public  final JTextArea             area;

	public FoodCalculator() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// --- Загрузка данных из файла ---
		try (Scanner sc = new Scanner(
				new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("FoodList.txt")),
						StandardCharsets.UTF_8
				)
		)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line.isEmpty()) continue;
				String[] parts = line.split(",", 5);
				String name = parts[0].trim();
				double c = parseDouble(parts[1]);
				double p = parseDouble(parts[2]);
				double f = parseDouble(parts[3]);
				double h = parseDouble(parts[4]);
				foodlist.add(new Food(name, c, p, f, h));
			}
		}
        // --- Построение строк продуктов ---
		for (Food f : foodlist) {
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
			row.add(new JLabel(String.format(
					"%s (100г→%dккал, Б%.1f/Ж%.1f/У%.1f):",
					f.name, (int)f.calPer100, f.protPer100, f.fatPer100, f.carbPer100
			)));
			JTextField tf = new JTextField(5);
			fields.add(tf);
			row.add(tf);
			add(row);
		}

		// --- Кнопки ---
		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
		calcBtn  = new JButton("Посчитать КБЖУ");
		saveBtn  = new JButton("Сохранить рецепт");
		findBtn  = new JButton("Найти рецепт");
		clearBtn = new JButton("Очистить всё");
		btnRow.add(calcBtn);
		btnRow.add(saveBtn);
		btnRow.add(findBtn);
		btnRow.add(clearBtn);
		add(btnRow);

		// --- Область вывода ---
		area = new JTextArea(15, 40);
		area.setEditable(false);
		add(Box.createVerticalStrut(5));
		add(new JScrollPane(
				area,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		));

		// --- Лисенеры ---
		calcBtn.addActionListener(e -> calculate());
		saveBtn.addActionListener(e -> {
			calculate();
			new SaveRecipe(area);
		});
		findBtn.addActionListener(e -> new FindRecipe(area));
		clearBtn.addActionListener(e -> {
			fields.forEach(fld -> fld.setText(""));
			area.setText("");
		});
	}

	/** Очищает от всего, кроме цифр и точки, и парсит */
	private double parseDouble(String raw) {
		String cleaned = raw.replaceAll("[^0-9.]", "");
		if (cleaned.isEmpty()) return 0.0;
		return Double.parseDouble(cleaned);
	}

	private void calculate() {
		double totC=0, totP=0, totF=0, totH=0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < foodlist.size(); i++) {
			String txt = fields.get(i).getText().trim();
			if (txt.isEmpty()) continue;
			double g = Double.parseDouble(txt);
			Food f = foodlist.get(i);
			double c = f.cpg()*g;
			double p = f.ppg()*g;
			double fr= f.fpg()*g;
			double h = f.kpg()*g;
			totC+=c; totP+=p; totF+=fr; totH+=h;
			sb.append(String.format(
					"%s: %.1fг → %.0fккал, Б%.1fг, Ж%.1fг, У%.1fг%n",
					f.name, g, c, p, fr, h
			));
		}
		sb.append(String.format(
				"%nИтого:%nКалории: %.0fккал%nБелки: %.1fг%nЖиры: %.1fг%nУглеводы: %.1fг",
				totC, totP, totF, totH
		));
		area.setText(sb.toString());
	}
}

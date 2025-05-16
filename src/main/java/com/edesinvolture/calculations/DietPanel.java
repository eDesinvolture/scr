package com.edesinvolture.calculations;

import javax.swing.*;
import java.awt.*;

public class DietPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField weightField, heightField, ageField;
    private JComboBox<String> genderBox, activityBox;
    private JTextField targetWeightField, monthsField;
    private JButton activityHelpButton, calcButton;
    private JTextArea resultArea;

    public DietPanel() {
        setBorder(BorderFactory.createTitledBorder("Калькулятор суточной нормы"));
        setLayout(new BorderLayout(10,10));

        // --- Вводные поля ---
        JPanel input = new JPanel(new GridLayout(8, 2, 5, 5));
        input.add(new JLabel("Вес (кг):"));
        weightField = new JTextField();
        input.add(weightField);

        input.add(new JLabel("Рост (см):"));
        heightField = new JTextField();
        input.add(heightField);

        input.add(new JLabel("Возраст (лет):"));
        ageField = new JTextField();
        input.add(ageField);

        input.add(new JLabel("Пол:"));
        genderBox = new JComboBox<>(new String[]{"Мужчина", "Женщина"});
        input.add(genderBox);

        input.add(new JLabel("Уровень активности:"));
        JPanel activityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        activityBox = new JComboBox<>(new String[]{
                "Сидячий (×1.2)",
                "Лёгкая (×1.375)",
                "Умеренная (×1.55)",
                "Высокая (×1.725)"
        });
        activityHelpButton = new JButton("?");
        activityHelpButton.setMargin(new Insets(2, 5, 2, 5));
        activityHelpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Коэффициенты активности:\n" +
                            "• Сидячий образ жизни: 1.2\n" +
                            "• Лёгкая (1–3 тренировки/нед): 1.375\n" +
                            "• Умеренная (3–5 тренировок/нед): 1.55\n" +
                            "• Высокая (6–7 тренировок/нед): 1.725",
                    "Подсказка по активности",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        activityPanel.add(activityBox);
        activityPanel.add(activityHelpButton);
        input.add(activityPanel);

        // Новые поля
        input.add(new JLabel("Целевой вес (кг):"));
        targetWeightField = new JTextField();
        input.add(targetWeightField);

        input.add(new JLabel("Срок (мес):"));
        monthsField = new JTextField();
        input.add(monthsField);

        // Кнопка расчёта
        input.add(new JLabel(""));
        calcButton = new JButton("Рассчитать");
        input.add(calcButton);

        add(input, BorderLayout.NORTH);

        // Область результата
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Лисенер
        calcButton.addActionListener(e -> calculate());
    }

    private void calculate() {
        try {
            double weight   = Double.parseDouble(weightField.getText().trim());
            double heightCm = Double.parseDouble(heightField.getText().trim());
            double heightM  = heightCm / 100.0;
            int age         = Integer.parseInt(ageField.getText().trim());
            boolean isMale  = genderBox.getSelectedIndex() == 0;
            double[] factors = {1.2, 1.375, 1.55, 1.725};
            double activityFactor = factors[activityBox.getSelectedIndex()];

            if (weight <= 0 || heightM <= 0 || age <= 0 || heightCm <= 0) {
                JOptionPane.showMessageDialog(this, "Вес, рост и возраст должны быть положительными числами", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Общие расчёты
            double bmi         = weight / (heightM * heightM);
            double idealBMIW   = 22.5 * heightM * heightM;
            double bmr = isMale
                    ? 10*weight + 6.25*heightCm - 5*age + 5
                    : 10*weight + 6.25*heightCm - 5*age - 161;
            double tdee = bmr * activityFactor;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("ИМТ: %.2f\n", bmi))
                    .append(String.format("BMR (Миффлин Сан Жеор) - базовый уровень обмена веществ: %.0f ккал\n", bmr))
                    .append(String.format("TDEE - общая суточная тратa энергии (с уч. активности): %.0f ккал\n\n", tdee));

            // Пользовательская цель?
            String tgtW = targetWeightField.getText().trim();
            String tgtM = monthsField.getText().trim();
            if (!tgtW.isEmpty() && !tgtM.isEmpty()) {
                double targetWeight = Double.parseDouble(tgtW);
                int months = Integer.parseInt(tgtM);
                double days = months * 30.0;

                double delta = weight - targetWeight;
                // 1 кг жира ≈7700 ккал
                double dailyDeficit = (delta * 7700) / days;
                double calorieGoal  = tdee - dailyDeficit;

                sb.append(String.format(
                                "Чтобы дойти от %.1f до %.1f кг за %d мес:\n", weight, targetWeight, months))
                        .append(String.format("  • Ежедневный %s: %.0f ккал\n",
                                delta>0 ? "дефицит" : "профицит", Math.abs(dailyDeficit)))
                        .append(String.format("  • Целевое потребление: %.0f ккал/день",
                                calorieGoal));
            } else {
                // Расчёт по идеальному ИМТ
                sb.append(String.format("Идеальный вес (ИМТ=22.5): %.1f кг\n", idealBMIW));
                double delta = weight - idealBMIW;
                double dailyDeficit = (delta * 7700) / (30.0*6); // грубо за 6 мес
                double calorieGoal  = tdee - dailyDeficit;
                sb.append(String.format(
                                "Предложенный план (6 мес):\n  • Дефицит ≈%.0f ккал/день\n", dailyDeficit))
                        .append(String.format("  • Целевая калорийность: %.0f ккал/день",
                                calorieGoal));
            }

            resultArea.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Неверный ввод! Проверьте все поля.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

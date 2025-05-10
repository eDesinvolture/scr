package com.edesinvolture.steps;

import javax.swing.*;
import java.awt.*;

public class StepTrackerGUI extends JPanel {
    private final StepTracker tracker;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> dayBox;
    private final JTextField stepsField;
    private final JTextField goalField;
    private final JTextArea statsArea;

    public StepTrackerGUI() {
        super(new BorderLayout(5,5));
        tracker = new StepTracker();

        // --- Панель ввода сверху ---
        JPanel input = new JPanel(new GridLayout(6, 2, 5, 5));

        // 1. Месяц
        input.add(new JLabel("Месяц:"));
        monthBox = new JComboBox<>(new String[]{
                "1 (Январь)","2 (Февраль)","3 (Март)","4 (Апрель)",
                "5 (Май)","6 (Июнь)","7 (Июль)","8 (Август)",
                "9 (Сентябрь)","10 (Октябрь)","11 (Ноябрь)","12 (Декабрь)"
        });
        input.add(monthBox);

        // 2. День
        input.add(new JLabel("День:"));
        dayBox = new JComboBox<>();
        input.add(dayBox);
        monthBox.addActionListener(e -> updateDays());
        updateDays();

        // 3. Шаги
        input.add(new JLabel("Шагов:"));
        stepsField = new JTextField();
        input.add(stepsField);

        // 4. Новая цель
        input.add(new JLabel("Новая цель шагов (в день):"));
        goalField = new JTextField();
        input.add(goalField);

        // 5. Пустая ячейка + кнопка смены цели
        input.add(new JLabel(""));
        JButton goalBtn = new JButton("Установить цель");
        input.add(goalBtn);

        // 6. Кнопки добавления и показа статистики
        JButton addBtn   = new JButton("Добавить шаги");
        JButton statsBtn = new JButton("Показать статистику");
        input.add(addBtn);
        input.add(statsBtn);

        add(input, BorderLayout.NORTH);

        // --- Область вывода статистики ---
        statsArea = new JTextArea(12, 40);
        statsArea.setEditable(false);
        add(new JScrollPane(statsArea), BorderLayout.CENTER);

        // --- Лисенеры ---

        // Добавление шагов
        addBtn.addActionListener(e -> {
            String txt = stepsField.getText().trim();
            if (txt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите количество шагов", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int steps = Integer.parseInt(txt);
                if (steps < 0) throw new NumberFormatException();
                int month = monthBox.getSelectedIndex() + 1;
                int day   = (Integer)dayBox.getSelectedItem();
                tracker.addStepsDay(steps, month, day);
                JOptionPane.showMessageDialog(this, "Шаги сохранены");
                stepsField.setText("");

                statsArea.setText(tracker.getStatistic(month));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Неверный формат: введите неотрицательное целое число",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Показать статистику
        statsBtn.addActionListener(e -> {
            int month = monthBox.getSelectedIndex() + 1;
            statsArea.setText(tracker.getStatistic(month));
        });

        // Смена цели
        goalBtn.addActionListener(e -> {
            String txt = goalField.getText().trim();
            if (txt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Введите новую цель шагов", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int goal = Integer.parseInt(txt);
                if (goal < 0) throw new NumberFormatException();
                tracker.changeGoal(goal);
                JOptionPane.showMessageDialog(this, "Новая цель сохранена: " + goal);
                goalField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Неверный формат: введите неотрицательное целое число",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Обновляем дни при смене месяца
    private void updateDays() {
        int month = monthBox.getSelectedIndex() + 1;
        int days  = StepTracker.daysInMonth(month);
        dayBox.removeAllItems();
        for (int d = 1; d <= days; d++) {
            dayBox.addItem(d);
        }
    }
}

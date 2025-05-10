package com.edesinvolture.steps;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.Objects;
import java.util.StringTokenizer;

public class StepTracker {
    private final int[][] trackArray = new int[12][31]; // индексы 0–30 соответствуют дням 1–31
    private int avrInDay = 10000;
    private static final File SAVE_FILE = new File("data/steps.dat");




    public StepTracker() {
        load(); // при создании подгружаем данные
    }

    public void addStepsDay(int steps, int month, int day) {
        // month: 1–12, day: 1–daysInMonth
        trackArray[month - 1][day - 1] += steps;
        save();
    }

    public String getStatistic(int month) {
        Converter converter = new Converter();
        int days = daysInMonth(month);
        double sum = 0.0;
        int[] maxDay = {0, 0};
        int nMax = 0, countMax = 0;

        StringBuilder sb = new StringBuilder("Статистика за месяц:\n");
        for (int d = 1; d <= days; d++) {
            int steps = trackArray[month - 1][d - 1];
            sb.append(d).append(" день: ").append(steps).append(" шагов\n");
            if (steps > maxDay[0]) {
                maxDay[0] = steps;
                maxDay[1] = d;
            }
            sum += steps;
            if (steps >= avrInDay) {
                nMax++;
                if (nMax > countMax) countMax = nMax;
            } else {
                nMax = 0;
            }
        }

        double avr = sum / days;
        sb.append("\nИтого шагов: ").append((int)sum)
                .append("\nМаксимум в ").append(maxDay[1]).append(" день: ").append(maxDay[0])
                .append("\nСреднее в день: ").append(String.format("%.2f", avr))
                .append("\nКм пройдено: ").append(String.format("%.2f", converter.kmInSteps(sum)))
                .append("\nКкал сожжено: ").append(String.format("%.2f", converter.kKalInSteps(sum)))
                .append("\nЛучшая серия: ").append(countMax)
                .append("\nТекущая цель: ").append(avrInDay).append(" шагов/день\n");

        return sb.toString();
    }

    public void changeGoal(int newGoal) {
        if (newGoal >= 0) {
            avrInDay = newGoal;
            save();
        }
    }

    public static int daysInMonth(int month) {
        switch (month) {
            case  4: case  6: case  9: case 11: return 30;
            case  2:
                int year = Year.now().getValue();
                boolean leap = (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
                return leap ? 29 : 28;
            default: return 31;
        }
    }
    private void parseFromReader(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line != null) avrInDay = Integer.parseInt(line.trim());
        for (int m = 0; m < 12; m++) {
            line = in.readLine();
            if (line == null) break;
            StringTokenizer st = new StringTokenizer(line, ",");
            int days = daysInMonth(m + 1);
            for (int d = 1; d <= days && st.hasMoreTokens(); d++) {
                trackArray[m][d - 1] = Integer.parseInt(st.nextToken().trim());
            }
        }
    }


    private void save() {
        try {
            SAVE_FILE.getParentFile().mkdirs();
            try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
                out.println(avrInDay);
                for (int m = 0; m < 12; m++) {
                    int days = daysInMonth(m + 1);
                    for (int d = 1; d <= days; d++) {
                        out.print(trackArray[m][d - 1]);
                        out.print(',');
                    }
                    out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        if (SAVE_FILE.exists()) {
            try (BufferedReader in = new BufferedReader(new FileReader(SAVE_FILE))) {
                parseFromReader(in);
                return;
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Если пользовательского файла нет — загружаем дефолтный из ресурсов
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("steps.dat")
                ),
                StandardCharsets.UTF_8
        ))) {
            parseFromReader(in);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }


}

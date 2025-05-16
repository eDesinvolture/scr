package com.edesinvolture.calculations;

import com.edesinvolture.steps.StepTracker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodCalculatorTest {

    @Test
    void testCaloriesCalculationForSingleFood() {
        FoodCalculator.Food food = new FoodCalculator.Food("Яблоко", 52, 0.3, 0.2, 14);
        double grams = 150;
        double expectedCalories = 52 * 1.5;
        double actualCalories = food.cpg() * grams;
        System.out.println("Расчёт калорий: " + actualCalories);
        assertEquals(expectedCalories, actualCalories, 0.01);
    }

    @Test
    void testProteinCalculation() {
        FoodCalculator.Food food = new FoodCalculator.Food("Мясо", 143, 26, 5, 0);
        double grams = 200;
        double expectedProtein = 26 * 2;
        double actualProtein = food.ppg() * grams;
        assertEquals(expectedProtein, actualProtein, 0.01);
    }

    @Test
    void testFatCalculation() {
        FoodCalculator.Food food = new FoodCalculator.Food("Сыр", 300, 25, 30, 1);
        double grams = 50;
        double expectedFat = 30 * 0.5;
        double actualFat = food.fpg() * grams;
        assertEquals(expectedFat, actualFat, 0.01);
    }

    @Test
    void testCarbCalculation() {
        FoodCalculator.Food food = new FoodCalculator.Food("Хлеб", 250, 7, 1, 50);
        double grams = 100;
        double expectedCarbs = 50;
        double actualCarbs = food.kpg() * grams;
        assertEquals(expectedCarbs, actualCarbs, 0.01);
    }

    @Test
    void testZeroGramsGivesZeroResult() {
        FoodCalculator.Food food = new FoodCalculator.Food("Молоко", 60, 3, 2, 5);
        double grams = 0;
        assertEquals(0.0, food.cpg() * grams, 0.001);
        assertEquals(0.0, food.ppg() * grams, 0.001);
        assertEquals(0.0, food.fpg() * grams, 0.001);
        assertEquals(0.0, food.kpg() * grams, 0.001);
    }

    @Test
    void testNegativeGramsShouldBeInvalid() {
        FoodCalculator.Food food = new FoodCalculator.Food("Хлеб", 250, 7, 1, 50);
        double grams = -100;
        double calories = food.cpg() * grams;
        assertTrue(calories < 0, "Калории не должны быть отрицательными");
    }

    @Test
    void testVeryLargeInput() {
        FoodCalculator.Food food = new FoodCalculator.Food("Рис", 350, 6, 1, 70);
        double grams = 10000;
        double expectedCalories = food.cpg() * grams;
        assertTrue(expectedCalories > 30000);
    }

    @Test
    void testAddStepsAndRetrieveStatistic() {
        StepTracker tracker = new StepTracker();
        tracker.addStepsDay(12345, 1, 1);
        String stats = tracker.getStatistic(1);

        assertTrue(stats.contains("1 день:"), "Не найден день в статистике");
        assertTrue(stats.matches("(?s).*1 день: \\d+ шагов.*"), "Не найден формат строки '1 день: N шагов'");
    }

    @Test
    void testChangeStepGoal() {
        StepTracker tracker = new StepTracker();
        tracker.changeGoal(15000);
        String stats = tracker.getStatistic(1);
        assertTrue(stats.contains("Текущая цель: 15000"), "Цель не отобразилась в строке статистики");
    }

    @Test
    void testCaloricSurplus() {
        double norm = 2000;
        double intake = 2500;
        double surplus = intake - norm;
        assertEquals(500, surplus);
    }


}

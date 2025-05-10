package com.edesinvolture.main;


import com.edesinvolture.calculations.DietPanel;
import com.edesinvolture.calculations.FoodCalculator;
import com.edesinvolture.steps.StepTrackerGUI;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainGUI() {
        super("Трекер калорий, шагов и диеты");
        setLayout(new BorderLayout());

        // -- левая панель: FoodCalculator
        FoodCalculator foodPanel = new FoodCalculator();
        JScrollPane foodScroll = new JScrollPane(
                foodPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // -- правая часть: шаги и диета
        StepTrackerGUI stepPanel = new StepTrackerGUI();
        DietPanel dietPanel = new DietPanel();
        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                stepPanel,
                dietPanel
        );
        rightSplit.setResizeWeight(0.5);
        rightSplit.setDividerLocation(350);

        // -- главный сплит: слева еда, справа шаги+диета
        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                foodScroll,
                rightSplit
        );
        mainSplit.setResizeWeight(0.5);
        mainSplit.setDividerLocation(600);

        add(mainSplit, BorderLayout.CENTER);

        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}

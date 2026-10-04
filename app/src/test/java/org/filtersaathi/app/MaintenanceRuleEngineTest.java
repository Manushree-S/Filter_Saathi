package org.filtersaathi.app;

import org.filtersaathi.app.domain.engine.MaintenanceRuleEngine;
import org.filtersaathi.app.domain.model.WaterQualityParameter;
import org.filtersaathi.app.domain.model.WaterQualityStatus;
import org.junit.Test;
import static org.junit.Assert.*;

public class MaintenanceRuleEngineTest {

    @Test
    public void testHealthScoreCalculation_normalConditions() {
        int score = MaintenanceRuleEngine.calculateFilterHealthScore(90, 365, 730, false);
        assertEquals(100, score);
    }

    @Test
    public void testHealthScoreCalculation_withBreakdownFault() {
        int score = MaintenanceRuleEngine.calculateFilterHealthScore(90, 365, 730, true);
        assertEquals(35, score);
    }

    @Test
    public void testTdsEvaluation_safeRange() {
        WaterQualityParameter param = MaintenanceRuleEngine.evaluateTds(150.0f);
        assertEquals(WaterQualityStatus.SAFE, param.getStatus());
    }

    @Test
    public void testTdsEvaluation_unsafeRange() {
        WaterQualityParameter param = MaintenanceRuleEngine.evaluateTds(650.0f);
        assertEquals(WaterQualityStatus.UNSAFE, param.getStatus());
    }

    @Test
    public void testPhEvaluation_safeRange() {
        WaterQualityParameter param = MaintenanceRuleEngine.evaluatePh(7.4f);
        assertEquals(WaterQualityStatus.SAFE, param.getStatus());
    }
}

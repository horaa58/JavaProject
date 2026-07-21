package smartparkingmanagementsystem;

/** Lightweight deterministic Java 17 test runner; no external framework required. */
public final class AllTests {
    private AllTests() { }
    public static void main(String[] args) throws Exception {
        ProductionDataSnapshot productionSnapshot = ProductionDataSnapshot.capture();
        TestSuite suite = new TestSuite();
        suite.setPrefix("SER ");
        SerializationModelTest.register(suite);
        FileHandlingTest.register(suite);
        IntegrationTest.register(suite);
        suite.setPrefix("REG ");
        ParkingSlotManagerTest.register(suite);
        VehicleManagerTest.register(suite);
        ModuleOneReportManagerTest.register(suite);
        suite.setPrefix("UI  ");
        GuiSmokeTest.register(suite);
        suite.setPrefix("SER ");
        SerializationSafetyTest.register(suite, productionSnapshot);
        suite.summary();
    }
}

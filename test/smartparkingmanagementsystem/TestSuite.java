package smartparkingmanagementsystem;

import java.util.Objects;

final class TestSuite {
    private int passed;
    private int failed;
    private String prefix = "";

    void setPrefix(String prefix) { this.prefix = prefix == null ? "" : prefix; }

    void test(String name, CheckedRunnable test) {
        try { test.run(); passed++; System.out.println("PASS  " + prefix + name); }
        catch (Throwable error) { failed++; System.err.println("FAIL  " + prefix + name + " -> " + error); }
    }

    void summary() {
        System.out.println(); System.out.println("Passed: " + passed); System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
        if (failed > 0) throw new AssertionError(failed + " test(s) failed");
    }

    static void equal(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
    }
    static void close(double expected, double actual) { if (Math.abs(expected - actual) > .00001) throw new AssertionError("expected " + expected + " but was " + actual); }
    static void truth(boolean condition) { if (!condition) throw new AssertionError("condition was false"); }
    static void rejects(CheckedRunnable operation) {
        try { operation.run(); throw new AssertionError("expected IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
        catch (Exception other) { throw new AssertionError(other); }
    }
    static void throwsType(Class<? extends Throwable> type, CheckedRunnable operation) {
        try { operation.run(); throw new AssertionError("expected " + type.getSimpleName()); }
        catch (Throwable actual) {
            if (!type.isInstance(actual)) throw new AssertionError("expected " + type.getSimpleName() + " but was " + actual, actual);
        }
    }

    @FunctionalInterface interface CheckedRunnable { void run() throws Exception; }
}

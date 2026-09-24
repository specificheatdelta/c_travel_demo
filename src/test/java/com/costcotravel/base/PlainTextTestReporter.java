package com.costcotravel.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/**
 * Writes each test result into Surefire's text report. Surefire's own text
 * report lists only the summary when every test passes.
 */
public class PlainTextTestReporter implements TestExecutionListener {

    private final Map<String, Long> startedAtNanos = new HashMap<>();
    private final List<TestResultLine> results = new ArrayList<>();
    private String testClass = "tests";

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            startedAtNanos.put(testIdentifier.getUniqueId(), System.nanoTime());
        }
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        if (testIdentifier.isTest()) {
            rememberClass(testIdentifier);
            results.add(new TestResultLine("SKIP", 0, testIdentifier.getDisplayName()));
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (!testIdentifier.isTest()) {
            return;
        }
        rememberClass(testIdentifier);
        String status = switch (testExecutionResult.getStatus()) {
            case SUCCESSFUL -> "PASS";
            case FAILED -> "FAIL";
            case ABORTED -> "ABORT";
        };
        results.add(new TestResultLine(status, elapsedSeconds(testIdentifier), testIdentifier.getDisplayName()));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        if (results.isEmpty()) {
            return;
        }
        long passed = results.stream().filter(result -> result.status.equals("PASS")).count();
        long failed = results.stream().filter(result -> result.status.equals("FAIL")).count();
        long aborted = results.stream().filter(result -> result.status.equals("ABORT")).count();
        long skipped = results.stream().filter(result -> result.status.equals("SKIP")).count();
        double totalSeconds = results.stream().mapToDouble(result -> result.seconds).sum();

        List<String> lines = new ArrayList<>();
        lines.add("Test set: " + testClass);
        lines.add("");
        lines.add(String.format("%-6s  %-10s  %s", "Result", "Time", "Test"));
        for (TestResultLine result : results) {
            lines.add(String.format(Locale.US, "%-6s  %7.3f s   %s", result.status, result.seconds, result.name));
        }
        lines.add("");
        lines.add("Passed: " + passed + "  Failed: " + failed + "  Aborted: " + aborted + "  Skipped: " + skipped);
        lines.add(String.format(Locale.US, "Total runtime: %.3f s", totalSeconds));

        Path report = Path.of("target", "surefire-reports", testClass + ".txt");
        try {
            Files.createDirectories(report.getParent());
            Files.write(report, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not write " + report, ex);
        }
    }

    private double elapsedSeconds(TestIdentifier testIdentifier) {
        Long started = startedAtNanos.remove(testIdentifier.getUniqueId());
        if (started == null) {
            return 0;
        }
        return (System.nanoTime() - started) / 1_000_000_000.0;
    }

    private void rememberClass(TestIdentifier testIdentifier) {
        testIdentifier.getSource().ifPresent(source -> {
            if (source instanceof MethodSource method) {
                testClass = method.getClassName();
            }
        });
    }

    private record TestResultLine(String status, double seconds, String name) {
    }
}

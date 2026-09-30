package com.costcotravel.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.WebDriver;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

/**
 * Runs axe-core against the current page and writes a credential-free summary.
 */
public final class AxeReport {

    private AxeReport() {
    }

    public static List<Rule> scan(WebDriver driver, List<String> includeSelectors) {
        AxeBuilder builder = new AxeBuilder()
                .withTags(List.of("wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22aa"));
        if (includeSelectors != null && !includeSelectors.isEmpty()) {
            builder.include(includeSelectors);
        }
        Results results = builder.analyze(driver);
        return results.getViolations() == null ? List.of() : results.getViolations();
    }

    public static List<Rule> blocking(List<Rule> violations) {
        List<Rule> blocking = new ArrayList<>();
        for (Rule rule : violations) {
            String impact = rule.getImpact() == null ? "" : rule.getImpact().toLowerCase(Locale.US);
            if (impact.equals("serious") || impact.equals("critical")) {
                blocking.add(rule);
            }
        }
        return blocking;
    }

    public static void write(String fileName, String pageTitle, String url, List<Rule> violations) {
        List<String> lines = new ArrayList<>();
        lines.add("Page: " + pageTitle);
        lines.add("URL: " + publicUrl(url));
        lines.add("Violation rules: " + violations.size());
        lines.add("Serious or critical: " + blocking(violations).size());
        lines.add("");
        if (violations.isEmpty()) {
            lines.add("No WCAG 2.x A/AA violations reported by axe-core.");
        }
        for (Rule rule : violations) {
            lines.add("[" + safe(rule.getImpact()) + "] " + safe(rule.getId()) + " — " + safe(rule.getHelp()));
            lines.add("  " + safe(rule.getHelpUrl()));
            List<CheckedNode> nodes = rule.getNodes() == null ? List.of() : rule.getNodes();
            int shown = 0;
            for (CheckedNode node : nodes) {
                if (shown == 3) {
                    lines.add("  ... " + (nodes.size() - shown) + " more nodes");
                    break;
                }
                lines.add("  target: " + target(node));
                String summary = node.getFailureSummary();
                if (summary != null && !summary.isBlank()) {
                    lines.add("  " + summary.replace("\n", " ").replaceAll("\\s+", " ").trim());
                }
                shown++;
            }
            lines.add("");
        }
        Path report = Path.of("reports", fileName);
        try {
            Files.createDirectories(report.getParent());
            Files.write(report, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not write " + report, ex);
        }
    }

    private static String target(CheckedNode node) {
        Object target = node.getTarget();
        if (target == null) {
            return "(no target)";
        }
        if (target instanceof List<?> list) {
            return list.isEmpty() ? "(no target)" : String.valueOf(list.get(0));
        }
        return String.valueOf(target);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String publicUrl(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        int query = url.indexOf('?');
        String bare = query < 0 ? url : url.substring(0, query);
        if (bare.contains("signin.costco.com")) {
            return "https://signin.costco.com/";
        }
        return bare;
    }
}

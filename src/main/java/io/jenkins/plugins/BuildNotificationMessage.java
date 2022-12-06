package io.jenkins.plugins;

import java.util.ArrayList;
import java.util.List;

public class BuildNotificationMessage {

    private final String title;
    private final String result;
    private final List<String> changes;
    private final String url;
    private final String urlTitle;

    public BuildNotificationMessage(String title, String result, List<String> changes, String url, String urlTitle) {
        this.title = title;
        this.result = result;
        this.changes = changes;
        this.url = url;
        this.urlTitle = urlTitle;
    }

    @Override
    public String toString() {
        List<String> lines = new ArrayList<>();
        lines.add(title);
        lines.add("");

        lines.add(result);
        lines.add("");

        if (!changes.isEmpty()) {
            lines.addAll(changes);
            lines.add("");
        }
        lines.add(String.format("%s <%s>", urlTitle, url));
        lines.add("");

        StringBuilder buffer = new StringBuilder();
        for (String line: lines) {
            buffer.append(line).append("\n");
        }
        return buffer.toString();
    }

}

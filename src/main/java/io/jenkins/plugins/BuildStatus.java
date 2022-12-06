package io.jenkins.plugins;

import hudson.model.Result;
import hudson.model.Run;

public enum BuildStatus {

    /**
     * Represents a build that has failed
     */
    BROKEN("Broken"),
    /**
     * Represents a build that has failed after a failed build
     */
    STILL_BROKEN("Still Broken"),
    /**
     * Represents a build that has succeeded after a failed build
     */
    FIXED("Fixed"),
    /**
     * Represents a build that has succeeded (if there is a previous build, it has been succeeded too)
     */
    SUCCESSFUL("Successful");

    private final String tag;

    BuildStatus(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return this.tag;
    }

    public static BuildStatus of(Run<?,?> build) {
        Run<?,?> previousBuild = build.getPreviousBuild();
        if (build.getResult() == Result.SUCCESS) {
            if (previousBuild != null) {
                return previousBuild.getResult() == Result.SUCCESS ? SUCCESSFUL : FIXED;
            } else {
                return SUCCESSFUL;
            }
        } else {
            if (previousBuild != null) {
                return previousBuild.getResult() != Result.SUCCESS ? STILL_BROKEN : BROKEN;
            } else {
                return BROKEN;
            }
        }
    }

}

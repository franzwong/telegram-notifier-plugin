package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelegramNotifier extends Notifier implements SimpleBuildStep {

    private final String defaultChatIds;

    private boolean sendIfSuccess;

    private String successfulChatIds;

    private String brokenChatIds;

    private String stillBrokenChatIds;

    private String fixedChatIds;

    @DataBoundConstructor
    public TelegramNotifier(String defaultChatIds) {
        this.defaultChatIds = isNullOrBlank(defaultChatIds) ? null : defaultChatIds.trim();
        this.sendIfSuccess = false;

        this.successfulChatIds = null;
        this.brokenChatIds = null;
        this.stillBrokenChatIds = null;
        this.fixedChatIds = null;
    }

    public String getDefaultChatIds() {
        return defaultChatIds;
    }

    public boolean isSendIfSuccess() {
        return sendIfSuccess;
    }

    public String getSuccessfulChatIds() {
        return successfulChatIds;
    }

    public String getBrokenChatIds() {
        return brokenChatIds;
    }

    public String getStillBrokenChatIds() {
        return stillBrokenChatIds;
    }

    public String getFixedChatIds() {
        return fixedChatIds;
    }

    @DataBoundSetter
    public void setSendIfSuccess(boolean sendIfSuccess) {
        this.sendIfSuccess = sendIfSuccess;
    }

    @DataBoundSetter
    public void setSuccessfulChatIds(String successfulChatIds) {
        this.successfulChatIds = isNullOrBlank(successfulChatIds) ? null : successfulChatIds.trim();
    }

    @DataBoundSetter
    public void setBrokenChatIds(String brokenChatIds) {
        this.brokenChatIds = isNullOrBlank(brokenChatIds) ? null : brokenChatIds.trim();
    }

    @DataBoundSetter
    public void setStillBrokenChatIds(String stillBrokenChatIds) {
        this.stillBrokenChatIds = isNullOrBlank(stillBrokenChatIds) ? null : stillBrokenChatIds.trim();
    }

    @DataBoundSetter
    public void setFixedChatIds(String fixedChatIds) {
        this.fixedChatIds = isNullOrBlank(fixedChatIds) ? null : fixedChatIds.trim();
    }

    @Override
    public void perform(@NonNull Run<?,?> build, @NonNull FilePath workspace, @NonNull EnvVars env,
                        @NonNull Launcher launcher, @NonNull TaskListener listener)
        throws IOException {
        if (isNullOrBlank(defaultChatIds)) {
            throw new AbortException("No default chat IDs defined");
        }

        DescriptorImpl descriptor = (DescriptorImpl) getDescriptor();
        String botToken = descriptor.getBotToken();
        if (isNullOrBlank(botToken)) {
            throw new AbortException("No bot token defined");
        }

        TelegramApi api = new TelegramApi(botToken);

        BuildStatus status = BuildStatus.of(build);
        if (status != BuildStatus.SUCCESSFUL || sendIfSuccess) {
            BuildNotificationMessage message = createMessage(build);
            try {
                String[] chatIds = getChatIdsByBuildStatus(status);
                api.sendMessage(chatIds, message.toString());
            } catch (TelegramApiException e) {
                e.printStackTrace(listener.getLogger());
            }
        }
    }

    private String[] getChatIdsByBuildStatus(BuildStatus status) {
        String chatIds = null;
        switch (status) {
            case SUCCESSFUL:
                chatIds = successfulChatIds;
                break;
            case BROKEN:
                chatIds = brokenChatIds;
                break;
            case STILL_BROKEN:
                chatIds = stillBrokenChatIds;
                break;
            case FIXED:
                chatIds = fixedChatIds;
                break;
        }
        if (chatIds == null) {
            chatIds = this.defaultChatIds;
        }
        List<String> chatIdList = new ArrayList<>();
        for (String chatId : chatIds.split("\\s*,\\s*")) {
            if (!isNullOrBlank(chatId)) {
                chatIdList.add(chatId);
            }
        }
        return chatIdList.toArray(new String[0]);
    }

    private BuildNotificationMessage createMessage(Run<?,?> build) {
        BuildStatus status = BuildStatus.of(build);

        String title = String.format(
            "%s - Build #%d of %s",
            status.tag(),
            build.getNumber(),
            build.getParent().getName()
        );

        Result buildResult = build.getResult();
        String result = buildResult == null ? "ONGOING" : buildResult.toString();

        List<String> changes = new ArrayList<>();
        if (build instanceof WorkflowRun) {
            WorkflowRun workflowRun = (WorkflowRun) build;

            if (workflowRun.getChangeSets().size() > 0) {
                for (ChangeLogSet<? extends ChangeLogSet.Entry> changeLog: workflowRun.getChangeSets()) {
                    for (ChangeLogSet.Entry change : changeLog) {
                        changes.add(String.format("%s - %s", change.getMsg(), change.getAuthor()));
                    }
                }
            }
        }

        String baseUrl = Jenkins.get().getRootUrl();
        String url = String.format("%s%s", baseUrl, build.getUrl());
        String urlTitle = "Go to build";

        return new BuildNotificationMessage(title, result, changes, url, urlTitle);
    }

    private boolean isNullOrBlank(String value) {
        return (value == null || value.trim().isEmpty());
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String botToken;

        public DescriptorImpl() {
            load();
        }

        public String getBotToken() {
            return botToken;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            JSONObject config = json.getJSONObject("telegram");
            this.botToken = config.getString("botToken");
            save();
            return true;
        }

        @Override
        @NonNull
        public String getDisplayName() {
            return "Telegram Notifier Plugin";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }

}

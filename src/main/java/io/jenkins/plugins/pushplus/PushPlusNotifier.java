package io.jenkins.plugins.pushplus;

import java.io.IOException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.verb.POST;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;

/**
 * Sends build result notifications via pushplus.
 */
public class PushPlusNotifier extends Notifier implements SimpleBuildStep {

    private String topic;
    private String channel;
    private String webhook;
    private String to;

    public String getTopic() {
        return topic;
    }

    public String getChannel() {
        return channel;
    }

    public String getWebhook() {
        return webhook;
    }

    public String getTo() {
        return to;
    }

    @DataBoundConstructor
    public PushPlusNotifier(String topic, String channel, String webhook, String to) {
        super();
        this.topic = topic;
        this.channel = channel;
        this.webhook = webhook;
        this.to = to;
    }

    @Override
    public boolean requiresWorkspace() {
        return false;
    }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull EnvVars env, @NonNull TaskListener listener)
            throws InterruptedException, IOException {
        Result result = run.getResult();
        if (null != result && result.equals(Result.FAILURE)) {
            listener.getLogger().println(Messages.PushPlusNotifier_BuildFailure());
            new PushPlusServiceImpl(run, listener, this).failure();
        } else if (null != result && result.equals(Result.ABORTED)) {
            listener.getLogger().println(Messages.PushPlusNotifier_BuildAborted());
            new PushPlusServiceImpl(run, listener, this).aborted();
        } else if (null != result && result.equals(Result.UNSTABLE)) {
            listener.getLogger().println(Messages.PushPlusNotifier_BuildUnstable());
            new PushPlusServiceImpl(run, listener, this).unstable();
        } else {
            listener.getLogger().println(Messages.PushPlusNotifier_BuildSuccess());
            new PushPlusServiceImpl(run, listener, this).success();
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol("pushplus")
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.PushPlusNotifier_DescriptorImpl_DisplayName();
        }

        /** @deprecated use {@link PushPlusGlobalConfiguration#getReturnUrl()} */
        @Deprecated
        public String getReturnUrl() {
            return PushPlusGlobalConfiguration.get().getReturnUrl();
        }

        /** @deprecated use {@link PushPlusGlobalConfiguration#getTokenId()} */
        @Deprecated
        public String getTokenId() {
            Secret token = PushPlusGlobalConfiguration.get().getTokenId();
            return token != null ? token.getPlainText() : null;
        }

        @POST
        public ListBoxModel doFillChannelItems(@AncestorInPath Item item) {
            if (item == null) {
                Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            } else {
                item.checkPermission(Item.CONFIGURE);
            }
            ListBoxModel items = new ListBoxModel();
            items.add(Messages.PushPlusNotifier_ChannelWechat(), "wechat");
            items.add(Messages.PushPlusNotifier_ChannelApp(), "app");
            items.add(Messages.PushPlusNotifier_ChannelExtension(), "extension");
            items.add(Messages.PushPlusNotifier_ChannelWebhook(), "webhook");
            items.add(Messages.PushPlusNotifier_ChannelClawBot(), "clawbot");
            items.add(Messages.PushPlusNotifier_ChannelCp(), "cp");
            items.add(Messages.PushPlusNotifier_ChannelMail(), "mail");
            items.add(Messages.PushPlusNotifier_ChannelSms(), "sms");
            items.add(Messages.PushPlusNotifier_ChannelVoice(), "voice");
            return items;
        }
    }
}

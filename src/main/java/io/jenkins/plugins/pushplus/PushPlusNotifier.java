package io.jenkins.plugins.pushplus;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import static hudson.Util.nullify;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * @version 1.0
 * @ClassName PushPlusNotify
 * @Description
 * @Author zhangheng
 * @Date 2019/11/21 15:51
 **/
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
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        Result result = run.getResult();
        if (null != result && result.equals(Result.FAILURE)) {
            listener.getLogger().println("项目构建失败,推送通知到pushplus");
            new PushPlusServiceImpl(run, listener, this).failure();
        } else if (null != result && result.equals(Result.ABORTED)) {
            listener.getLogger().println("项目构建被终止,推送通知到pushplus");
            new PushPlusServiceImpl(run, listener, this).aborted();
        } else if (null != result && result.equals(Result.UNSTABLE)) {
            listener.getLogger().println("项目状态不稳定,推送通知到pushplus");
            new PushPlusServiceImpl(run, listener, this).unstable();
        } else {
            //项目未出现任何异常报错
            listener.getLogger().println("推送通知到pushplus");
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
        private String returnUrl;

        private String tokenId;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // Most of this stuff is the same as the built-in email publisher

            this.returnUrl = nullify(formData.getString("returnUrl"));
            this.tokenId = nullify(formData.getString("tokenId"));
            save();
            return super.configure(req, formData);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }


        @Override
        public String getDisplayName() {
            return "plusplus";
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public ListBoxModel doFillChannelItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("微信公众号(wechat)", "wechat");
            items.add("第三方webhook(webhook)", "webhook");
            items.add("企业微信应用(cp)", "cp");
            items.add("邮件(mail)", "mail");
            items.add("短信(sms)", "sms");
            items.add("浏览器插件(extension)", "extension");
            return items;
        }
    }
}

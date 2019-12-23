package io.jenkins.plugins.push;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

import static hudson.Util.nullify;

/**
 * @version 1.0
 * @ClassName PushPlusNotify
 * @Description
 * @Author zhangheng(中道)
 * @Date 2019/11/21 15:51
 **/
public class PushPlusNotifier extends Notifier implements SimpleBuildStep {


    private String topic;

    public String getTopic() {
        return topic;
    }

    @DataBoundConstructor
    public PushPlusNotifier(String topic) {
        super();
        this.topic = topic;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        Result result = run.getResult();
        if (null != result && result.equals(Result.FAILURE)) {
            listener.getLogger().println("项目构建失败,推送通知到push+");
            new PushPlusServiceImpl(run, listener, this).failure();
        } else if (null != result && result.equals(Result.ABORTED)) {
            listener.getLogger().println("项目构建被终止,推送通知到push+");
            new PushPlusServiceImpl(run, listener, this).aborted();
        } else if (null != result && result.equals(Result.UNSTABLE)) {
            listener.getLogger().println("项目状态不稳定,推送通知到push+");
            new PushPlusServiceImpl(run, listener, this).unstable();
        } else {
            //项目未出现任何异常报错
            listener.getLogger().println("推送通知到push+");
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
    }
}

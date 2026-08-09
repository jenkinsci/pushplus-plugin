package io.jenkins.plugins.pushplus;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;

/**
 * Global pushplus settings shown on Manage Jenkins &gt; System.
 */
@Extension
@Symbol("pushplusGlobalConfig")
public class PushPlusGlobalConfiguration extends GlobalConfiguration {

    private String returnUrl;
    private Secret tokenId;

    public PushPlusGlobalConfiguration() {
        load();
    }

    public static PushPlusGlobalConfiguration get() {
        return GlobalConfiguration.all().get(PushPlusGlobalConfiguration.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.PushPlusNotifier_DescriptorImpl_DisplayName();
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    @DataBoundSetter
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        save();
    }

    public Secret getTokenId() {
        return tokenId;
    }

    @DataBoundSetter
    public void setTokenId(Secret tokenId) {
        this.tokenId = tokenId;
        save();
    }
}

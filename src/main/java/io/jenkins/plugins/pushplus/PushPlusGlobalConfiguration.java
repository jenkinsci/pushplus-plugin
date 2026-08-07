package io.jenkins.plugins.pushplus;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

/**
 * Global pushplus settings shown on Manage Jenkins &gt; System.
 */
@Extension
@Symbol("pushplusGlobalConfig")
public class PushPlusGlobalConfiguration extends GlobalConfiguration {

    private String returnUrl;
    private String tokenId;

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

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    @DataBoundSetter
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getTokenId() {
        return tokenId;
    }

    @DataBoundSetter
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}

package io.jenkins.plugins.pushplus;

import java.io.IOException;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * @version 1.0
 * @ClassName PushPlusServiceImpl
 * @Description
 * @Author zhangheng
 * @Date 2019/11/21 18:29
 **/
public class PushPlusServiceImpl implements PushPlusService {

    private static final String DEFAULT_URL = "https://www.pushplus.plus/send/%s/jenkins";

    private Run<?, ?> run;

    private TaskListener listener;

    private PushPlusNotifier pushPlusNotifier;

    public PushPlusServiceImpl(Run<?, ?> run, TaskListener listener, PushPlusNotifier pushPlusNotifier) {
        this.run = run;
        this.listener = listener;
        this.pushPlusNotifier = pushPlusNotifier;
    }

    @Override
    public void success() {
        String title = Messages.PushPlusServiceImpl_TitleSuccess(run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushError());
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushErrorDetail(e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void failure() {
        String title = Messages.PushPlusServiceImpl_TitleFailure(run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushError());
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushErrorDetail(e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void aborted() {
        String title = Messages.PushPlusServiceImpl_TitleAborted(run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushError());
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushErrorDetail(e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void unstable() {
        String title = Messages.PushPlusServiceImpl_TitleUnstable(run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushError());
            listener.getLogger().println(Messages.PushPlusServiceImpl_PushErrorDetail(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * @param title 标题
     */
    private void push(String title) throws IOException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        Cause.UserIdCause cause = this.run.getCause(Cause.UserIdCause.class);
        String buildUser = "";
        if (cause != null) {
            buildUser = cause.getUserName();
        }
        String buildNumber = run.getEnvironment(listener).get("BUILD_NUMBER");

        String buildState = this.run.getResult() != null ? this.run.getResult().toString() : "";
        String projectUrl = this.pushPlusNotifier.getDescriptor().getReturnUrl() + this.run.getUrl();
        String projectLogUrl = this.pushPlusNotifier.getDescriptor().getReturnUrl() + this.run.getUrl() + "/console";

        long costTime = (System.currentTimeMillis() - run.getStartTimeInMillis()) / 1000;

        String url = String.format(DEFAULT_URL, this.pushPlusNotifier.getDescriptor().getTokenId().trim());

        if (StrUtil.isNotEmpty(this.pushPlusNotifier.getTopic())) {
            jsonObject.put("topic", this.pushPlusNotifier.getTopic());
        }
        if (StrUtil.isNotEmpty(this.pushPlusNotifier.getChannel())) {
            jsonObject.put("channel", this.pushPlusNotifier.getChannel());
        }
        if (StrUtil.isNotEmpty(this.pushPlusNotifier.getWebhook())) {
            jsonObject.put("webhook", this.pushPlusNotifier.getWebhook());
        }
        if (StrUtil.isNotEmpty(this.pushPlusNotifier.getTo())) {
            jsonObject.put("to", this.pushPlusNotifier.getTo());
        }
        jsonObject.put("title", title);
        jsonObject.put("buildState", buildState);
        jsonObject.put("projectName", this.run.getFullDisplayName());
        jsonObject.put("buildNumber", buildNumber);
        jsonObject.put("buildUser", buildUser);
        jsonObject.put("buildLogUrl", projectLogUrl);
        jsonObject.put("projectUrl", projectUrl);
        jsonObject.put("costTime", costTime + "");

        String body = HttpRequest.post(url).body(jsonObject.toString(), "application/json").execute().body();

        System.out.println(body);
    }
}

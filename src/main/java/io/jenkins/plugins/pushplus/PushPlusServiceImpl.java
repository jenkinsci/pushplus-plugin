package io.jenkins.plugins.pushplus;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.IOException;

/**
 * @version 1.0
 * @ClassName PushPlusServiceImpl
 * @Description 处理具体消息逻辑
 * @Author zhangheng(中道)
 * @Date 2019/11/21 18:29
 **/
public class PushPlusServiceImpl implements PushPlusService {

    private static final String DEFAULT_URL = "http://pushplus.hxtrip.com/customer/jenkinsPush/sendJenkinsMessage";

    private Run<?, ?> run;
    /**
     * 监听器
     */
    private TaskListener listener;

    private PushPlusNotifier pushPlusNotifier;

    public PushPlusServiceImpl(Run<?, ?> run, TaskListener listener, PushPlusNotifier pushPlusNotifier) {
        this.run = run;
        this.listener = listener;
        this.pushPlusNotifier = pushPlusNotifier;
    }

    @Override
    public void success() {

        String title = String.format("%s构建成功", run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println("推送微信>>>获取微参数失败");
            e.printStackTrace();
        }


    }

    @Override
    public void failure() {
        String title = String.format("%s构建失败", run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println("推送微信>>>获取微参数失败");
            e.printStackTrace();
        }
    }

    @Override
    public void aborted() {
        String title = String.format("%s终止构建", run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println("推送微信>>>获取微参数失败");
            e.printStackTrace();
        }

    }

    @Override
    public void unstable() {
        String title = String.format("%s终止构建", run.getFullDisplayName());
        try {
            push(title);
        } catch (Exception e) {
            listener.getLogger().println("推送微信>>>获取微参数失败");
            listener.getLogger().println("推送微信>>>获取微参数失败:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     *
     *
     *
     */
    /**
     * @param title 标题
     */
    private void push(String title) throws IOException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        Cause.UserIdCause userIdCause = (Cause.UserIdCause) this.run.getCause(Cause.UserIdCause.class);
        String buildUser = userIdCause.getUserName();
        String buildNumber = "";
        buildNumber = run.getEnvironment(listener).get("BUILD_NUMBER");
        //获取构建状态

        String buildState = this.run.getResult() != null ? this.run.getResult().toString() : "";
        String projectUrl = this.pushPlusNotifier.getDescriptor().getReturnUrl() + this.run.getUrl();
        String projectLogUrl = this.pushPlusNotifier.getDescriptor().getReturnUrl() + this.run.getUrl() + "/console";

        long costTime = (System.currentTimeMillis() - run.getStartTimeInMillis()) / 1000;

        jsonObject.put("token", this.pushPlusNotifier.getDescriptor().getTokenId().trim());
        jsonObject.put("topic", this.pushPlusNotifier.getTopic());
        jsonObject.put("title", title);
        jsonObject.put("buildState", buildState);
        jsonObject.put("projectName", this.run.getFullDisplayName());
        jsonObject.put("buildNumber", buildNumber);
        jsonObject.put("buildUser", buildUser);
        jsonObject.put("buildLogUrl", projectLogUrl);

        jsonObject.put("projectUrl", projectUrl);
        jsonObject.put("costTime", costTime + "");

        String body = HttpRequest.post(DEFAULT_URL).body(jsonObject).execute().body();

    }


}

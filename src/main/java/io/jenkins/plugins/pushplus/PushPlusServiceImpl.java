package io.jenkins.plugins.pushplus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import hudson.model.Cause;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.Secret;
import net.sf.json.JSONObject;

/**
 * Sends build notifications to the pushplus HTTP API.
 */
public class PushPlusServiceImpl implements PushPlusService {

    private static final String DEFAULT_URL = "https://www.pushplus.plus/send/";

    private final Run<?, ?> run;
    private final TaskListener listener;
    private final PushPlusNotifier pushPlusNotifier;

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
            e.printStackTrace(listener.getLogger());
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
            e.printStackTrace(listener.getLogger());
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
            e.printStackTrace(listener.getLogger());
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
            e.printStackTrace(listener.getLogger());
        }
    }

    private void push(String title) throws IOException, InterruptedException {
        Cause.UserIdCause cause = this.run.getCause(Cause.UserIdCause.class);
        String buildUser = "";
        if (cause != null) {
            buildUser = cause.getUserName();
        }
        String buildNumber = run.getEnvironment(listener).get("BUILD_NUMBER");
        if (buildNumber == null) {
            buildNumber = "";
        }

        Result result = this.run.getResult();
        String buildState = result != null ? result.toString() : "";
        PushPlusGlobalConfiguration global = PushPlusGlobalConfiguration.get();
        String jenkinsUrl = global != null && global.getReturnUrl() != null ? global.getReturnUrl() : "";
        String projectUrl = jenkinsUrl + this.run.getUrl();
        String projectLogUrl = jenkinsUrl + this.run.getUrl() + "console";

        long costTime = (System.currentTimeMillis() - run.getStartTimeInMillis()) / 1000;

        Secret tokenSecret = global != null ? global.getTokenId() : null;
        String token = tokenSecret != null ? tokenSecret.getPlainText() : "";
        if (token == null || token.isBlank()) {
            listener.getLogger().println(Messages.PushPlusServiceImpl_TokenMissing());
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token.trim());
        jsonObject.put("template", "jenkins");
        jsonObject.put("title", title);

        putIfNotBlank(jsonObject, "topic", this.pushPlusNotifier.getTopic());
        putIfNotBlank(jsonObject, "channel", this.pushPlusNotifier.getChannel());
        putIfNotBlank(jsonObject, "webhook", this.pushPlusNotifier.getWebhook());
        putIfNotBlank(jsonObject, "to", this.pushPlusNotifier.getTo());

        JSONObject content = new JSONObject();
        content.put("buildState", buildState);
        content.put("projectName", this.run.getFullDisplayName());
        content.put("buildNumber", buildNumber);
        content.put("buildUser", buildUser);
        content.put("buildLogUrl", projectLogUrl);
        content.put("projectUrl", projectUrl);
        content.put("costTime", Long.toString(costTime));

        jsonObject.put("content", content);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEFAULT_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        listener.getLogger().println("pushplus >>> HTTP " + response.statusCode() + ": " + response.body());
    }

    private static void putIfNotBlank(JSONObject json, String key, String value) {
        if (value != null && !value.isBlank()) {
            json.put(key, value);
        }
    }
}

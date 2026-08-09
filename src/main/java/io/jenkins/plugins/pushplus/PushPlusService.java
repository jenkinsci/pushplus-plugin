package io.jenkins.plugins.pushplus;

/**
 * Sends pushplus notifications for the different Jenkins build results.
 */
public interface PushPlusService {
    /**
     * 请求成功
     */
    void success();

    /**
     * 失败
     */
    void failure();

    /**
     * 拒绝
     */
    void aborted();

    /**
     * 不稳定
     */
    void unstable();
}

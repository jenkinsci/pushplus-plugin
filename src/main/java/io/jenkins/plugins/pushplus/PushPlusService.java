package io.jenkins.plugins.pushplus;

/**
 * @version 1.0
 * @ClassName PushPlusService
 * @Description
 * @Author zhangheng(中道)
 * @Date 2019/11/21 18:23
 **/
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

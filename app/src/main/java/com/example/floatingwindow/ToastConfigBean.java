package com.example.floatingwindow;

import android.text.TextUtils;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义悬浮可点击Toast组件配置
 */
public class ToastConfigBean {

    public static final String TYPE_COLOR_BUTTON = "typeColorButton"; //面按钮
    public static final String TYPE_TITLE_BUTTON = "typeTitleButton"; //字按钮
    public static final String TYPE_ARROW_BUTTON = "typeArrowButton"; //箭头按钮

    public static final String TYPE_JUMP_LINK = "link"; // 跳转链接
    public static final String TYPE_OPEN_MINI_APP = "openMiniApp";// 跳转小程序
    public static final String JUMP_OFFLINE_POPUP = "offlinePopup";// 跳转离线弹窗
    public static final String JUMP_NEW_WEB_VIEW = "jumpNewWebView"; // 跳转到新容器

    public static final String TOAST_GUIDE_ALBUM = "TOAST_GUIDE_ALBUM";
    public static final String TOAST_GUIDE_PUBLISH_SHARE = "guidePublishShare"; //发布图文-点击分享，引导批量分享、引导输入法
    public static final String TOAST_GUIDE_SHARE_ALBUM = "guideShareAlbum"; //发布图文-点击发布，引导分享相册
    public static final String TOAST_GUIDE_TAG = "guideTag"; //发布图文-点击发布，引导商品分类
    public static final String TOAST_GUIDE_BATCH_PUBLISH = "guideBatchPublish"; //发布图文-点击发布，引导批量发布
    public static final String TOAST_GUIDE_BATCH_EDIT = "guideBatchEdit"; //编辑图文-保存成功，引导批量编辑
    public static final String TOAST_GUIDE_ALBUM_NUM = "guideAlbum"; //转发成功，引导查看我的主页
    public static final String TOAST_GUIDE_SHARE = "guideShare"; //分享图文，引导批量分享、引导输入法
    public static final String TOAST_GUIDE_SCRIPT = "guideScript"; //下载 引导输入法

    private String imageIcon;
    private String title;
    private List<ButtonBean> buttons;
    private int toastTime;
    private String type;
    private String guideType;
    private String refererTitle;

    public ToastConfigBean(String title) {
        this.title = title;
    }

    public ToastConfigBean(String imageIcon, String title) {
        this.imageIcon = imageIcon;
        this.title = title;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ButtonBean> getButtons() {
        return buttons;
    }

    public void setButtons(List<ButtonBean> buttons) {
        this.buttons = buttons;
    }

    public int getToastTime() {
        return toastTime;
    }

    public void setToastTime(int toastTime) {
        this.toastTime = toastTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGuideType() {
        return guideType;
    }

    public void setGuideType(String guideType) {
        this.guideType = guideType;
    }

    public String getRefererTitle() {
        return refererTitle;
    }

    public void setRefererTitle(String refererTitle) {
        this.refererTitle = refererTitle;
    }

    public static class ButtonBean{
        private String buttonTitle;
        private String buttonColor;
        private String buttonJumpMode;
        private String jumpHtmlUrl;
        private String nativeUrl;
        private String buttonType;
        private OfflineBean offlinePopup;
        private MiniAppBean openMiniApp;

        public String getButtonTitle() {
            return buttonTitle;
        }

        public void setButtonTitle(String buttonTitle) {
            this.buttonTitle = buttonTitle;
        }

        public String getButtonColor() {
            return buttonColor;
        }

        public void setButtonColor(String buttonColor) {
            this.buttonColor = buttonColor;
        }

        public String getButtonJumpMode() {
            return buttonJumpMode;
        }

        public void setButtonJumpMode(String buttonJumpMode) {
            this.buttonJumpMode = buttonJumpMode;
        }

        public String getJumpHtmlUrl() {
            return jumpHtmlUrl;
        }

        public void setJumpHtmlUrl(String jumpHtmlUrl) {
            this.jumpHtmlUrl = jumpHtmlUrl;
        }

        public String getNativeUrl() {
            return nativeUrl;
        }

        public void setNativeUrl(String nativeUrl) {
            this.nativeUrl = nativeUrl;
        }

        public String getButtonType() {
            return buttonType;
        }

        public void setButtonType(String buttonType) {
            this.buttonType = buttonType;
        }

        public OfflineBean getOfflinePopup() {
            return offlinePopup;
        }

        public void setOfflinePopup(OfflineBean offlinePopup) {
            this.offlinePopup = offlinePopup;
        }

        public MiniAppBean getOpenMiniApp() {
            return openMiniApp;
        }

        public void setOpenMiniApp(MiniAppBean openMiniApp) {
            this.openMiniApp = openMiniApp;
        }
    }

    public static class OfflineBean{
        private String packName;
        private String sign;
        private String downloadUrl;
        private String h5Version;
        private String appVersion;
        private String localUrl;
        private String networkUrl;
        private String parameter;

        public String getPackName() {
            return packName;
        }

        public void setPackName(String packName) {
            this.packName = packName;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getH5Version() {
            return h5Version;
        }

        public void setH5Version(String h5Version) {
            this.h5Version = h5Version;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getLocalUrl() {
            return localUrl;
        }

        public void setLocalUrl(String localUrl) {
            this.localUrl = localUrl;
        }

        public String getNetworkUrl() {
            return networkUrl;
        }

        public void setNetworkUrl(String networkUrl) {
            this.networkUrl = networkUrl;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }
    }

    public static class MiniAppBean{
        private String miniAppId;
        private String miniAppPath;

        public String getMiniAppId() {
            return miniAppId;
        }

        public void setMiniAppId(String miniAppId) {
            this.miniAppId = miniAppId;
        }

        public String getMiniAppPath() {
            return miniAppPath;
        }

        public void setMiniAppPath(String miniAppPath) {
            this.miniAppPath = miniAppPath;
        }
    }

    private static boolean timeIsToday(long timestamp) {
        Calendar currentTime = Calendar.getInstance();
        Calendar lastTime = Calendar.getInstance();
        lastTime.setTimeInMillis(timestamp);
        if (currentTime.get(Calendar.YEAR) == lastTime.get(Calendar.YEAR)) {
            return 0 == currentTime.get(Calendar.DAY_OF_YEAR) - lastTime.get(Calendar.DAY_OF_YEAR);
        }
        return false;
    }
}

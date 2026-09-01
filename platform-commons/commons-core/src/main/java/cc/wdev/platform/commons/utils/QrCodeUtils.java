package cc.wdev.platform.commons.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;

/**
 * 二维码工具类
 *
 * @author elvea
 */
public abstract class QrCodeUtils {

    public static String generate(String content) {
        QrConfig config = new QrConfig(600, 600);
        config.setMargin(3);
        return QrCodeUtil.generateAsBase64(content, config, "jpeg");
    }

}

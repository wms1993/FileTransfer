package com.wms.kuaichuan.core.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

/**
 * 缩略图工具类
 * <p>
 * Created by 王梦思 on 2016/11/14.
 * <p/>
 */
public class ScreenshotUtils {

    /**
     * 创建缩略图
     */
    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
        return bitmap;
    }


    /**
     * 将图片转换成指定宽高
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(source, width, height);
        return bitmap;
    }


}

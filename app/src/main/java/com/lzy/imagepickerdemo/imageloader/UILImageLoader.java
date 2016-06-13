package com.lzy.imagepickerdemo.imageloader;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/3/28
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */
import android.app.Activity;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;

public class UILImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height,int level) {
//        ImageSize size = new ImageSize(width, height);
//        ImageLoader.getInstance().displayImage("file://" + path, imageView, size);
    }

    @Override
    public void clearMemoryCache() {
    }
}

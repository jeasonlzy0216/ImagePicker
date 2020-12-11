package com.lzy.imagepicker.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.exifinterface.media.ExifInterface;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Bitmap工具类，主要是解决拍照旋转的适配
 *
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-03-20  13:27
 */
@SuppressWarnings("unused")
public class BitmapUtil {

    private BitmapUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取图片的旋转角度
     *
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(ContentResolver resolver, Uri uri) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            InputStream stream = resolver.openInputStream(uri);
            if (stream == null) return degree;
            ExifInterface exifInterface = new ExifInterface(stream);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    private static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 获取我们需要的整理过旋转角度的Uri
     * @param resolver  上下文环境
     * @param uri      路径
     * @return          正常的Uri
     */
    public static Uri getRotatedUri(ContentResolver resolver, Uri uri){
        InputStream stream = null;
        try {
            stream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            return  uri;
        }
        int degree = BitmapUtil.getBitmapDegree(resolver, uri);
        if (degree != 0){
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Bitmap newBitmap = BitmapUtil.rotateBitmapByDegree(bitmap,degree);
            return Uri.parse(MediaStore.Images.Media.insertImage(resolver,newBitmap,null,null));
        }else{
            return uri;
        }
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param path   需要旋转的图片的路径
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotateBitmapByDegree(bitmap,degree);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri, Rect outPadding, BitmapFactory.Options opts) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, outPadding, opts);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

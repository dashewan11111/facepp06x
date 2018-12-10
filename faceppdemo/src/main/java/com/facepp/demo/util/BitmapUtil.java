package com.facepp.demo.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author by licheng on 2017/8/8.
 */

public class BitmapUtil {

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * base64转为bitmap
     *
     * @return
     */
    public static Bitmap base64ToBitmap2(Bitmap bmp) {
        byte[] bytes = toByteArray(bmp);
        byte[] newByte = byte2byte(bytes);
        return BitmapFactory.decodeByteArray(newByte, 0, newByte.length);
    }


    //将字符串转换成Bitmap类型
    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //将Bitmap转换成字符串
    public static String bitmaptoString(Bitmap bitmap) {
        String string;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public static File saveBitmapFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 根据人脸框裁剪人脸
     *
     * @param sourceBmp
     * @param rect
     * @return
     */
    public static Bitmap cropBitmap(Bitmap sourceBmp, Rect rect) {

        int x = 0 > rect.left ? 0 : rect.left;
        int y = 0 > rect.top ? 0 : rect.top;

        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;
        if (x + width > sourceBmp.getWidth()) {
            width -= (x + width) - sourceBmp.getWidth();
        }

        if (y + height > sourceBmp.getHeight()) {
            height -= (y + height) - sourceBmp.getHeight();
        }

        return Bitmap.createBitmap(sourceBmp, x, y, width, height);
    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @return
     */
    public static float[] byte2float(byte[] b) {

        float[] result = new float[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = getUint8(b[i]);
        }
        return result;
    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @return
     */
    public static byte[] byte2byte(byte[] b) {

        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            if (getUint8(b[i]) >= 120) {
                bytes.add(b[i]);
            }
        }
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }


    public static float getUint8(byte s) {
        return s & 0x00ff;
    }


    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void byte2File(byte[] buf, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        Log.e("inSampleSize", roundedSize + "");
        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static final String TAG = BitmapUtil.class.getSimpleName();


    /**
     * 根据Uri获取路径
     *
     * @param contentUri
     * @return
     */
    public static String getRealPathByURI(Uri contentUri, Context context) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    public static Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        String status = Environment.getExternalStorageState();
        SimpleDateFormat timeFormatter = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
        Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
        return imageFilePath;
    }

    /**
     * 图片压缩
     *
     * @param file
     */
    public static void compressBmpToFile(File file, int height, int width) {
        Bitmap bmp = decodeSampledBitmapFromFile(file.getPath(), height, width);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        /*while (baos.toByteArray().length / 1024 > 30) {
            baos.reset();
			if (options - 10 > 0) {
				options = options - 10;
				bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			if (options - 10 <= 0) {
				break;
			}
		}*/
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片变成bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getImageBitmap(String path) {
        Bitmap bitmap = null;
        File file = new File(path);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }


//=================================图片压缩方法===============================================

    /**
     * 质量压缩
     *
     * @param image
     * @param maxkb
     * @return
     * @author ping 2015-1-5 下午1:29:58
     */
    public static Bitmap compressBitmap(Bitmap image, int maxkb) {
        //L.showlog(压缩图片);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于(maxkb)50kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxkb) {
            // 重置baos即清空baos
            baos.reset();
            if (options - 10 > 0) {
                // 每次都减少10
                options -= 10;
            }
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * @param res
     * @param resId
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param filepath  图片路径
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    /**
     * @param bitmap
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] data = baos.toByteArray();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * 计算压缩比例值(改进版 by touch_ping)
     * <p>
     * 原版2>4>8...倍压缩
     * 当前2>3>4...倍压缩
     *
     * @param options   解析图片的配置信息
     * @param reqWidth  所需图片压缩尺寸最小宽度O
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int picheight = options.outHeight;
        final int picwidth = options.outWidth;

        int targetheight = picheight;
        int targetwidth = picwidth;
        int inSampleSize = 1;

        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight >= reqHeight
                    && targetwidth >= reqWidth) {
                inSampleSize += 1;
                targetheight = picheight / inSampleSize;
                targetwidth = picwidth / inSampleSize;
            }
        }

        Log.i("===", "最终压缩比例:" + inSampleSize + "倍");
        Log.i("===", "新尺寸:" + targetwidth + "*" + targetheight);
        return inSampleSize;
    }

    // 读取图像的旋转度
    public static int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * @param mBitmap
     * @param fileName
     */
    public static void saveBitmapToLocal(Bitmap mBitmap, String fileName) {
        if (mBitmap != null) {
            FileOutputStream fos = null;
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                fos = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 将下载下来的图片保存到SD卡或者本地.并返回图片的路径(包括文件命和扩展名)
     *
     * @param context
     * @param bitName
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, String bitName, Bitmap mBitmap) {
        String path = null;
        File f;
        if (mBitmap != null) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "images/");
                String fileName = Environment.getExternalStorageDirectory() + File.separator + "images/" + bitName + ".png";
                path = fileName;
                FileOutputStream fos = null;
                try {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    File file = new File(fileName);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                //本地存储路径
                f = new File(context.getFilesDir() + File.separator + "images/");
                Log.i(TAG, "本地存储路径:" + context.getFilesDir() + File.separator + "images/" + bitName + ".png");
                path = context.getFilesDir() + File.separator + "images/" + bitName + ".png";
                FileOutputStream fos = null;
                try {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    File file = new File(path);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return path;
    }

    /**
     * 删除图片
     *
     * @param context
     * @param bitName
     */
    public void deleteFile(Context context, String bitName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dirFile = new File(Environment.getExternalStorageDirectory() + File.separator + "images/" + bitName + ".png");
            if (!dirFile.exists()) {
                return;
            }

            dirFile.delete();
        } else {
            File f = new File(context.getFilesDir() + File.separator
                    + "images/" + bitName + ".png");
            if (!f.exists()) {
                return;
            }
            f.delete();
        }
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
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
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 处理旋转后的图片
     *
     * @param originpath 原图路径
     * @return 返回修复完毕后的图片路径
     */
    public static Bitmap amendRotatePhoto(String originpath, Bitmap bmp) {

        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);

        // 保存修复后的图片并返回保存后的图片路径
        return rotaingImageView(angle, bmp);
    }

    /**
     * 图片合成
     *
     * @param background 背景图
     * @param people     目标图
     * @return
     */
    public static Bitmap combine(Bitmap background, Bitmap people) {

        // 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap newBitmap = Bitmap.createBitmap(background);

        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();

        int w = background.getWidth();
        int h = background.getHeight();
        int w_2 = people.getWidth();
        int h_2 = people.getHeight();
        paint.setAlpha(0);
        canvas.drawRect(0, 0, background.getWidth(), background.getHeight(), paint);

        paint = new Paint();
        canvas.drawBitmap(people, Math.abs(w - w_2) / 2, Math.abs(h - h_2) / 2, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储新合成的图片
        canvas.restore();
        return newBitmap;
    }

}

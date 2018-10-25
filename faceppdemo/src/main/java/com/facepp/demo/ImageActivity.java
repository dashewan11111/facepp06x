package com.facepp.demo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facepp.demo.R;
import com.facepp.demo.util.ConUtil;
import com.megvii.facepp.sdk.Facepp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by xiejiantao on 2018/4/11.
 */

public class ImageActivity extends Activity implements View.OnClickListener {

    private Facepp facepp;
    private ImageView ivDetectFace;
    private Button btnChoose;
    private Button btnSave;

    private HandlerThread mHandlerThread = new HandlerThread("imagedetect");
    private Handler mHandler;

    Bitmap mOriginBitmap;
    Bitmap mDetectBitmap;

    View vEmpty;

    ProgressDialog progressDialog;


    int rotation = 0;


    public static final int GALLERY_CODE = 101;
    public static final int REQ_GALLERY_CODE = 101;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_layout);
        ivDetectFace = findViewById(R.id.iv_detect_face);
        btnChoose = findViewById(R.id.bt_choose_img);
        btnSave = findViewById(R.id.bt_save_img);
        btnChoose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        vEmpty = findViewById(R.id.ll_empty);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        initSdk();


    }


    private void initSdk() {
        facepp = new Facepp();
        String errorCode = facepp.init(this, ConUtil.getFileContent(this, R.raw.megviifacepp_model), 0);

        //sdk内部其他api已经处理好，可以不判断
        if (errorCode != null) {
            Intent intent = new Intent();
            intent.putExtra("errorcode", errorCode);
            setResult(101, intent);
            finish();
            return;
        }

        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
        faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_NORMAL;
        faceppConfig.is_smooth = false;
        faceppConfig.is_back_camera = true;

        faceppConfig.rotation = rotation;
        faceppConfig.face_confidence_filter = 0.6f;

        facepp.setFaceppConfig(faceppConfig);

    }

    File files[];
    FileWriter fw = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_choose_img:
//                requestGalleryPerm();
                ConUtil.acquireWakeLock(this);
                files = new File("/storage/emulated/0/miss").listFiles();
                Log.e("xie", "xie onClick: len=" + files.length);
                try {
                    fw = new FileWriter("/storage/emulated/0/megresultmiss.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < files.length; i++) {
                                mOriginBitmap = ConUtil.getBitmapWithPath(files[i].getAbsolutePath());    //get bitmap
                                if (mOriginBitmap==null){
                                    fw.write(files[i].getAbsolutePath() + " C " + "error" + "\n");

                                    continue;
                                }
                                byte[] data = ConUtil.getPixelsRGBA(mOriginBitmap);
                                byte[] gray=ConUtil.getGrayscale(mOriginBitmap);

                                final Facepp.Face[] faces = facepp.detect(gray, mOriginBitmap.getWidth(), mOriginBitmap.getHeight(), Facepp.IMAGEMODE_GRAY);
                                if (faces.length == 0) {

                                    fw.write(files[i].getAbsolutePath() + " N " + "noface" + "\n");


                                }
                                for (int j = 0; j < faces.length; j++) {
                                    fw.write(files[i].getAbsolutePath() + " " + j+" " + "confidence" + faces[j].confidence + "\n");

                                }
                            }
                            fw.close();

                            Log.e("xie", "run:image finish");

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("xie", "run:image finish catch");
                        }
                    }
                });
                break;
            case R.id.bt_save_img:
//                saveBitmap();
                break;
        }
    }


    private void requestGalleryPerm() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //进行权限请求
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_CODE);
        } else {
            openGalleryActivity();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == GALLERY_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted
                showSettingDialog("读取存储卡");
            } else {
                openGalleryActivity();
            }
        }
    }

    private void openGalleryActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_GALLERY_CODE);


    }


    public void showSettingDialog(String msg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_GALLERY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    processGalleryResult(uri);

                }
                break;

        }
    }


    private void processGalleryResult(final Uri uri) {
//        if (progressDialog == null) {
//            progressDialog = ProgressDialog.show(this, "人脸检测", "检测中。。。。。", false, true);
//            progressDialog.setCancelable(false);
//        } else {
//            progressDialog.show();
//        }
//        vEmpty.setVisibility(View.GONE);
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                String path = ConUtil.getRealPathFromURI(ImageActivity.this, uri);
//                mOriginBitmap = ConUtil.getBitmapWithPath(path);    //get bitmap
//                byte[] data = ConUtil.getPixelsRGBA(mOriginBitmap);
//
//
//
//                for (int i = 0; i < 4; i++) {
//                    rotation = (rotation + 90 * i) % 360;
//                    Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
//                    faceppConfig.rotation = rotation;
//                    facepp.setFaceppConfig(faceppConfig);
//
//                    final Facepp.Face[] faces = facepp.detect(data, mOriginBitmap.getWidth(), mOriginBitmap.getHeight(), Facepp.IMAGEMODE_RGBA);
//                    if (faces.length != 0) {
//                        mDetectBitmap = Bitmap.createBitmap(mOriginBitmap.getWidth(),
//                                mOriginBitmap.getHeight(), Bitmap.Config.RGB_565);
//
//                        Canvas canvas = new Canvas(mDetectBitmap);// 使用空白图片生成canvas
//                        // 将bmp1绘制在画布上
//                        Rect srcRect = new Rect(0, 0, mOriginBitmap.getWidth(), mOriginBitmap.getHeight());// 截取bmp1中的矩形区域
//                        Rect dstRect = new Rect(0, 0, mOriginBitmap.getWidth(), mOriginBitmap.getHeight());// bmp1在目标画布中的位置
//                        canvas.drawBitmap(mOriginBitmap, srcRect, dstRect, null);
//                        Paint paint = new Paint();
//                        paint.setColor(0xffff0000);
//
//                        paint.setStrokeWidth(5);
//
//                        for (int j = 0; j < faces.length; j++) {
//                            Facepp.Face face = faces[j];
//                            facepp.getLandmarkRaw(face, Facepp.FPP_GET_LANDMARK81);
//                            float[] points = ConUtil.getPoints(face.points);
//                            canvas.drawPoints(points, paint);
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ivDetectFace.setImageBitmap(mDetectBitmap);
//                                progressDialog.dismiss();
//                            }
//                        });
//
//                        return;
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        mDetectBitmap = null;
//                        ivDetectFace.setImageBitmap(mOriginBitmap);
//                        Toast.makeText(ImageActivity.this, "未检测到人脸", Toast.LENGTH_LONG).show();
//                    }
//                });
//
//            }
//        });

    }

    private void saveBitmap() {
        if (mDetectBitmap == null) {
            return;
        }

        String path = ConUtil.saveBitmap(this, mDetectBitmap);
        Toast.makeText(ImageActivity.this, "保存到" + path, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onDestroy() {
        mHandlerThread.quit();
        facepp.release();
        super.onDestroy();
    }
}

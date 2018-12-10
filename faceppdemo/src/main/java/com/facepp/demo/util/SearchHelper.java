package com.facepp.demo.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facepp.demo.R;
import com.megvii.facepp.api.FacePPApi;
import com.megvii.facepp.api.IFacePPCallBack;
import com.megvii.facepp.api.bean.SearchResponse;
import com.megvii.facepp.sdk.Facepp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by licheng on 2018/11/6.
 */

public class SearchHelper {

    private static SearchHelper instance;

    private int currentId = -1;

    private Map<String, byte[]> frames = new IdentityHashMap<>();

    FacePPApi faceppApi = new FacePPApi("syAe75QXfQHDt9YcmC8BJAJD0mX5nwqJ", "Q23rhNN6TsA8A6TcTOHkBsu-a7hBOUEB");

    private int rotation, width, height;

    private String faceset_token;

    private Context context;


    private SearchHelper(Context context, int rotation, int width, int height, String faceset_token) {
        this.context = context;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
        this.faceset_token = faceset_token;

    }

    public static SearchHelper getInstance(Context context, int rotation, int width, int height, String faceset_token) {
        if (null == instance) {
            synchronized (SearchHelper.class) {
                if (null == instance) {
                    instance = new SearchHelper(context, rotation, width, height, faceset_token);
                }
            }
        }
        return instance;
    }

    public void search(Facepp.Face face, byte[] frame) {
        if (currentId != face.trackID) {
            // ID 改变，加到请求队列
            addToQueue(face, frame);
        }
    }

    private void addToQueue(Facepp.Face face, byte[] frame) {
        switch (frames.size()) {
            case 0:
                frames.put(face.trackID + "", frame);
                break;
            case 1:
            case 2:
                addFrame(face.trackID, frame);
                break;
            case 3: // 满三帧，发请求
                currentId = face.trackID;
                doHttpPost(frames);
                frames.clear();
                break;
            default:
                break;
        }
    }

    private void addFrame(int trackId, byte[] frame) {
        if (!frames.keySet().toArray()[0].equals("" + trackId)) {
            // 不包含新数据的id, 与之前的数据不是一个人。
            frames.clear();
            frames.put(trackId + "", frame);
        } else {
            frames.put(trackId + "", frame);
        }
    }

    private void doHttpPost(Map<String, byte[]> frames) {

        Map<String, String> params = new HashMap<>();
        params.put("faceset_token", faceset_token);//2364ca01b84933b1d20b5792e6ec3d65

        List<byte[]> values = new ArrayList<>();
        values.addAll(frames.values());
        doPost(params, values, 0);
    }

    private void doPost(final Map<String, String> params, final List<byte[]> values, final int index) {
        if (index == values.size()) {
            return;
        }
        final YuvImage image = new YuvImage(values.get(index), ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap newBitmap = BitmapUtil.rotaingImageView(rotation, bmp);

        faceppApi.search(params, BitmapUtil.toByteArray(newBitmap), new IFacePPCallBack<SearchResponse>() {
            @Override
            public void onSuccess(final SearchResponse searchResponse) {
                Log.e("leee", "index : " + index);
                if (null != searchResponse.getResults() && searchResponse.getResults().size() > 0)
                    if (searchResponse.getResults().get(0).getConfidence() > 78) {
                        showResult(true, searchResponse.getResults().get(0).getConfidence() + "");
                    } else {
                        if (index == 2) {
                            showResult(false, searchResponse.getResults().get(0).getConfidence() + "");
                        } else {
                            doPost(params, values, index + 1);
                        }
                    }
            }

            @Override
            public void onFailed(final String error) {
                Log.e("leee", "error : " + error);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showResult(false, error);
                    }
                });

            }
        });

    }

    private void showResult(final boolean success, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(context);
                View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
                TextView content = view.findViewById(R.id.content);
                ImageView imageView = view.findViewById(R.id.image);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                imageView.setImageResource(success ? R.drawable.success : R.drawable.fail);
                content.setText(success ? "检索成功，置信度 ： " + message : "检索失败，" + message);
                toast.show();
            }
        });

    }
}

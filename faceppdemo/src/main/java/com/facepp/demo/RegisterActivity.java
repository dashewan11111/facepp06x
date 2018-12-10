package com.facepp.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.facepp.demo.util.BitmapUtil;
import com.megvii.facepp.api.FacePPApi;
import com.megvii.facepp.api.IFacePPCallBack;
import com.megvii.facepp.api.bean.DetectResponse;
import com.megvii.facepp.api.bean.FaceSetAddResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by licheng on 2018/11/9.
 */

public class RegisterActivity extends Activity {

    private String faceset_token;

    private FacePPApi facePPApi = new FacePPApi("syAe75QXfQHDt9YcmC8BJAJD0mX5nwqJ", "Q23rhNN6TsA8A6TcTOHkBsu-a7hBOUEB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        faceset_token = getIntent().getStringExtra("faceset_token");
    }

    public void btnAddFace(View view) {

        facePPApi.detect(new HashMap<String, String>(), BitmapUtil.toByteArray(BitmapFactory.decodeResource(getResources(), R.drawable.leee)), new IFacePPCallBack<DetectResponse>() {
            @Override
            public void onSuccess(DetectResponse detectResponse) {
                Map<String, String> params = new HashMap<>();
                params.put("face_tokens", detectResponse.getFaces().get(0).getFace_token());
                params.put("faceset_token", faceset_token);
                facePPApi.facesetAddFace(params, new IFacePPCallBack<FaceSetAddResponse>() {
                    @Override
                    public void onSuccess(FaceSetAddResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoadingActivity.class);
                                intent.putExtra("faceset_token", faceset_token);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onFailed(final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

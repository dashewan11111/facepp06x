package com.facepp.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.megvii.facepp.api.FacePPApi;
import com.megvii.facepp.api.IFacePPCallBack;
import com.megvii.facepp.api.bean.FaceSetCreatResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by licheng on 2018/11/9.
 */

public class SearchActivity extends Activity {

    private EditText name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
    }

    private void init() {
        name = findViewById(R.id.name);
    }

    public void btnCreateFaceset(View view) {
        if (TextUtils.isEmpty(name.getText())) {
            Toast.makeText(this, "name can not be null", Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("display_name", name.getText().toString().trim());
            new FacePPApi("syAe75QXfQHDt9YcmC8BJAJD0mX5nwqJ", "Q23rhNN6TsA8A6TcTOHkBsu-a7hBOUEB").facesetCreate(params, new IFacePPCallBack<FaceSetCreatResponse>() {
                @Override
                public void onSuccess(FaceSetCreatResponse faceSetCreatResponse) {
                    Intent intent = new Intent(SearchActivity.this, RegisterActivity.class);
                    intent.putExtra("faceset_token", faceSetCreatResponse.getFaceset_token());
                    startActivity(intent);
                }

                @Override
                public void onFailed(final String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }
    }
}

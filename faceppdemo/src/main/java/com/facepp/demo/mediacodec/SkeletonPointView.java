package com.facepp.demo.mediacodec;

/**
 * Created by caiyixin on 2018/8/8.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


import com.megvii.facepp.sdk.Facepp;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Locale;



public class SkeletonPointView extends View {
    public static final String TAG = SkeletonPointView.class.getSimpleName();
    private Paint 		m_paint;;
    private Matrix			m_drawMatrix;

    private Facepp.Face[] m_faces;

    private int m_camw;
    private int m_camh;
    private boolean m_isbackcamera;




    public SkeletonPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SkeletonPointView(Context	context){
        super(context);
        init();
    }


    private void init(){
        m_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        m_paint.setStrokeWidth(3);
        m_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        m_paint.setTextAlign(Paint.Align.CENTER);
        m_paint.setTextSize(35);

        m_drawMatrix = new Matrix();
        m_faces = null;
        m_camw=640;
        m_camh=480;
    }

//    public void		setSkeletonResult(SkeletonResult	sResult){
//        m_skeletonResult = sResult;
//        invalidate();
//    }
     public void setTextView(Facepp.Face[] faces ,int camw,int camh, boolean isbackcamera)
     {
         m_faces = faces;
         m_camw = camw;
         m_camh = camh;
         m_isbackcamera = isbackcamera;
         invalidate();
     }

    private void assignMatrixFromRelatedView(){
        m_drawMatrix.reset();
//        if(m_relatedImgView != null){
//            GLImageView imageView = m_relatedImgView.get();
//            if(imageView != null){
//                m_drawMatrix = imageView.makeImg2ViewMatrix(null);
//            }
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        m_paint.setColor(0xff006400);

        if(m_faces!=null)
        {
            for(int i = 0; i < m_faces.length;i++)
            {

                String id = String.valueOf(m_faces[i].trackID);

                String zx =  String.format("%.2f", m_faces[i].confidence);

                //String happy_c = String.format("%.2f", m_faces[i].happy);

                DecimalFormat decimalFormat = new DecimalFormat("00");
                String age =  decimalFormat.format(m_faces[i].age*100);

                String male = "male";
                if(m_faces[i].female > m_faces[i].male)
                {
                    male = "female";
                }

                //canvas.drawText("人脸编号    " +id+"   置信度    "+zx,200,100, m_paint);
                float a = m_faces[i].points[19].x;
                float b = m_faces[i].points[19].y;

                if(!m_isbackcamera)
                {
                    float tmp = a;
                    a = b;
                    b = m_camw - tmp;

                }
                else
                {
                    float tmp = a;
                    a = m_camh - b;
                    b = tmp;
                }

                float it = getWidth() - a/m_camh*getWidth();
                float j = b/m_camw*getHeight();
                canvas.drawText("id=" +id+"con= "+zx+" age= "+age +"sex="+male,it,j, m_paint);

            }

        }
        else
        {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

    }
    public void setViewLayout(RelativeLayout.LayoutParams layout_params )
    {
        this.setLayoutParams(layout_params);
    }


    public	void 	saveSkeletonPoints(String	strFileName){
//        StringBuilder	strCachePath = new StringBuilder(CommonSetting.SAVED_IMG_DIR);
//
//        }
    }
}

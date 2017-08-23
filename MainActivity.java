package com.example.u15161.opencvtste4;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {
    protected static final String TAG = "MYAPP::OPENCV";

    protected CameraBridgeViewBase mOpenCvCameraView;


    BaseLoaderCallback mCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
                    mOpenCvCameraView.enableView();

                    break;
                }
                default:
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this,mCallBack);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
/*
        Mat src = inputFrame.rgba();
        Mat cannyEdges = new Mat();
        MatOfPoint mop = new MatOfPoint();
        MatOfInt moi = new MatOfInt();


        //Imgproc.Canny(src, cannyEdges,10, 100);
        Imgproc.convexHull(mop,moi);

        return cannyEdges;
        */


        Mat rgba = inputFrame.rgba();
        org.opencv.core.Size sizeRgba = rgba.size();

        Mat rgbaInnerWindow;
        Mat cannyEdges = new Mat();

        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;

        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;

        //get sub-image
        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);

        //MatOfPoint mop = new MatOfPoint(rgbaInnerWindow);
        //MatOfInt moi = new MatOfInt(6);

        Imgproc.Canny(rgbaInnerWindow, cannyEdges, 10, 100);

        //Imgproc.convexHull(mop,moi);

        Imgproc.cvtColor(cannyEdges, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);

        List<MatOfPoint> pontos = new ArrayList<MatOfPoint>();


        Imgproc.findContours(rgbaInnerWindow,pontos,rgba,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for(int i=0; i<pontos.size(); i++)
        Imgproc.drawContours(rgbaInnerWindow, pontos, i, new Scalar(255.0, 255.0, 255.0), 5);

        rgbaInnerWindow.release();

        return rgba;
    }

}
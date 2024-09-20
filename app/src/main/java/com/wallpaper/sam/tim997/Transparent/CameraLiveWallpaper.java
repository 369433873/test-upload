package com.wallpaper.sam.tim997.Transparent;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;

public class CameraLiveWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new CameraEngine();
    }

    class CameraEngine extends Engine implements Camera.PreviewCallback {
        private Camera camera;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // 检查相机权限
            if (ContextCompat.checkSelfPermission(CameraLiveWallpaper.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startPreview();
            } else {
                Toast.makeText(CameraLiveWallpaper.this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                stopSelf();  // 如果没有权限，停止服务
            }

            setTouchEventsEnabled(true);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // 可以处理触摸事件，比如点击或长按拍照
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopPreview();  // 确保停止预览并释放相机资源
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                startPreview();
            } else {
                stopPreview();
            }
        }

        /**
         * 开始相机预览
         */
        public void startPreview() {
            try {
                // 打开相机
                camera = Camera.open(MainActivity.selectCamera);

                Camera.Parameters parameters = camera.getParameters();
                Camera.Size bestSize = null;

                // 自动对焦模式
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }

                // 根据屏幕分辨率选择最佳预览尺寸
                int x = MainActivity.width;
                int y = MainActivity.height;

                float tmp = 0f;
                float mindiff = 100f;
                float x_d_y = (float) x / (float) y;
                Camera.Size best = null;
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                for (Camera.Size s : supportedPreviewSizes) {
                    tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
                    if (tmp < mindiff) {
                        mindiff = tmp;
                        best = s;
                    }
                }

                // 设置预览大小和方向
                parameters.setPreviewSize(best.width, best.height);
                camera.setDisplayOrientation(90);

                // 设置预览显示到 SurfaceHolder
                camera.setPreviewDisplay(getSurfaceHolder());
                camera.setParameters(parameters);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
                releaseCamera();  // 处理相机打开失败情况
            }
        }

        /**
         * 停止相机预览并释放资源
         */
        public void stopPreview() {
            if (camera != null) {
                try {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    camera = null; // 确保释放后不再使用相机对象
                }
            }
        }


        /**
         * 释放相机资源
         */
        private void releaseCamera() {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }

        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            camera.addCallbackBuffer(bytes);
        }
    }
}

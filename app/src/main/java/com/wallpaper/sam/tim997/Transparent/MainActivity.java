package com.wallpaper.sam.tim997.Transparent;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    public Context mContext;
    public static int selectCamera = 0;
    public static int width = 1080;
    public static int height = 1920;

    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private int camAB = 1;
    private static final String AD_UNIT_ID = AdId.getMo1() + AdId.getMo2() + AdId.getMo4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadAd();

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                checkSelfPermission1();
            }
        });

        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                checkSelfPermission2();
            }
        });

        findViewById(R.id.text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelfPermission3();
            }
        });

        findViewById(R.id.text4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelfPermission4();
            }
        });
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, AD_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                MainActivity.this.interstitialAd = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        if (camAB == 1) {
                            startWallpaper1();
                        } else if (camAB == 2) {
                            startWallpaper2();
                        }
                        MainActivity.this.interstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        MainActivity.this.interstitialAd = null;
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkSelfPermission1() {
        camAB = 1;
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            if (interstitialAd != null) {
                interstitialAd.show(MainActivity.this);
            } else {
                startWallpaper1();
            }
        }
        loadAd();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkSelfPermission2() {
        camAB = 2;
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            if (interstitialAd != null) {
                interstitialAd.show(MainActivity.this);
            } else {
                startWallpaper2();
            }
        }
        loadAd();
    }

    void checkSelfPermission3() {
        try {
            String pkName = this.getPackageName();
            Intent localIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkName));
            startActivity(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkSelfPermission4() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.developer_name)));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (camAB == 1) {
                    startWallpaper1();
                } else {
                    startWallpaper2();
                }
            } else {
                Toast.makeText(mContext, "请打开相机权限", Toast.LENGTH_SHORT).show();
                // 如果权限被拒绝，跳转到设置
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    void startWallpaper1() {
        try {
            selectCamera = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent localIntent = new Intent();
                ComponentName localComponentName = new ComponentName(this, CameraLiveWallpaper.class);
                localIntent.setAction("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                localIntent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", localComponentName);
                startActivity(localIntent);
            } else {
                Intent localIntent = new Intent("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
                startActivity(localIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startWallpaper2() {
        try {
            selectCamera = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent localIntent = new Intent();
                ComponentName localComponentName = new ComponentName(this, CameraLiveWallpaper.class);
                localIntent.setAction("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                localIntent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", localComponentName);
                startActivity(localIntent);
            } else {
                Intent localIntent = new Intent("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
                startActivity(localIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAd();
    }

    @Override
    public void onPause() {
        super.onPause();
        loadAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

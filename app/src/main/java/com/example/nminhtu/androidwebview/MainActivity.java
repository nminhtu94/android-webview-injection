package com.example.nminhtu.androidwebview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sexample.nminhtu.androidwebview.R;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView.setWebContentsDebuggingEnabled(true);

        // Make activity fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        setupViews();
        setupConstraints();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.loadUrl("https://www.google.com");
    }

    @JavascriptInterface
    private void setupViews() {
        ConstraintLayout layout = findViewById(R.id.main_layout);

        mWebView = new WebView(MainActivity.this);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        final Activity activity = this;
        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
//                Log.d(TAG, "onPermissionRequest");
                // As we are targeting Android 21, there's no need to prompt for permission.
                // Just go ahead and grant the permission here.
                request.grant(request.getResources());
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view,
                                        int errorCode,
                                        String description,
                                        String failingUrl) {
                Toast.makeText(activity, "Oh! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                // Ignore SSL certificate errors - DANGEROUS, JUST FOR NOW
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Clear out speech hooks, etc. when the page is loaded
//                Log.i(TAG, "onPageStarted: Clearing user handlers!");
//                if (mSrec != null) mSrec.clearUserHandlers();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                injectCSS();
                super.onPageFinished(view, url);
            }
        });

        mWebView.setId(View.generateViewId());
        layout.addView(mWebView, 0);
    }

    private void injectCSS() {
        try {
            mWebView.evaluateJavascript(
                "javascript:(function() {" +
                "   console.log('Injecting code');" +
                "   let style = document.createElement('style');" +
                "   style.type = 'text/css';" +

                "   style.innerHTML = '" +
                "   head {" +
                "       -webkit-transform: translate3d(0, 0, 0);" +
                "       transform: translate3d(0, 0, 0);" +
                "   };" +
                "   body {" +
                "       -webkit-transform: translate3d(0, 0, 0);" +
                "       transform: translate3d(0, 0, 0);" +
                "   };';" +

                "   document.head.appendChild(style);" +
                "   document.body.appendChild(style);" +
                "})()",
                null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupConstraints() {
        ConstraintLayout layout = findViewById(R.id.main_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(
                mWebView.getId(),
                ConstraintSet.BOTTOM,
                R.id.main_layout,
                ConstraintSet.BOTTOM
        );

        constraintSet.connect(
                mWebView.getId(),
                ConstraintSet.TOP,
                R.id.main_layout,
                ConstraintSet.TOP
        );

        constraintSet.connect(
                mWebView.getId(),
                ConstraintSet.LEFT,
                R.id.main_layout,
                ConstraintSet.LEFT
        );

        constraintSet.connect(
                mWebView.getId(),
                ConstraintSet.RIGHT,
                R.id.main_layout,
                ConstraintSet.RIGHT
        );

        constraintSet.applyTo(layout);
    }
}

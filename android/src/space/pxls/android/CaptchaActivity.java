package space.pxls.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import space.pxls.Pxls;

public class CaptchaActivity extends Activity {
    private WebView view;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        key = intent.getStringExtra("token");

        view = new WebView(this);
        // We good, we good
        // only pxls code :)
        view.addJavascriptInterface(new Context(), "ctx");
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(Pxls.domain + "/mobile_captcha.html");
        setContentView(view);
    }

    public class Context {
        @JavascriptInterface
        public void finishCaptcha(String token) {
            Intent data = new Intent();
            data.putExtra("token", token);
            setResult(RESULT_OK, data);
            finish();
        }

        @JavascriptInterface
        public String getKey() {
            return key;
        }
    }
}

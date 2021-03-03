package space.pxls.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import space.pxls.Pxls;

public class CaptchaActivity extends Activity {
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        key = intent.getStringExtra("token");

        WebView view = new WebView(this);
        view.addJavascriptInterface(new Context(), "ctx");
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(Pxls.getDomain() + "/mobile_captcha.html");
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

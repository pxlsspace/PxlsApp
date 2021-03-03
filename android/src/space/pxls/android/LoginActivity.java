package space.pxls.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import space.pxls.Pxls;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        intent.getStringExtra("method");

        WebView view = new WebView(this);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(intent.getStringExtra("url"));
        view.setWebViewClient(new WebViewClient() {
            // Using the deprecated one for old API reasons, new one is 21+
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if (url.getPath().startsWith("/auth/")) {
                    Intent data = new Intent();
                    data.putExtra("url", url);
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
                return false;
            }
        });
        setContentView(view);
    }
}

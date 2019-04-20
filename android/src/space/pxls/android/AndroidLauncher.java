package space.pxls.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import space.pxls.ImageHelper;
import space.pxls.OrientationHelper;
import space.pxls.PxlsGame;
import space.pxls.VibrationHelper;

public class AndroidLauncher extends AndroidApplication {
    public static final int CAPTCHA_REQUEST = 1;
    public static final int LOGIN_VIEW = 2;

    private PxlsGame game;
    private PxlsGame.CaptchaCallback captchaCallback;

    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        String _v = "0.0.0";
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            _v = String.format("%s-%s", packageInfo.versionName, packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {/* ignored */}

        game = new PxlsGame(_v);
        game.orientationHelper = new OrientationHelper() {
            @Override
            public void setOrientation(Orientation orientation) {
                switch (orientation) {
                    case BEHIND:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
                        break;
                    case FULL_SENSOR:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        break;
                    case LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case NOSENSOR:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                        break;
                    case PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case REVERSE_LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    case REVERSE_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case SENSOR:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        break;
                    case SENSOR_LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        break;
                    case SENSOR_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        break;
                    case UNSPECIFIED:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        break;
                    case USER:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                        break;
                }
            }

            @Override
            public void setOrientation(SimpleOrientation orientation) {
                switch(orientation) {
                    case LANDSCAPE:
                        setOrientation(Orientation.SENSOR_LANDSCAPE);
                        break;
                    case PORTRAIT:
                        setOrientation(Orientation.SENSOR_PORTRAIT);
                        break;
                    case NA:
                        setOrientation(Orientation.FULL_SENSOR);
                        break;
                }
            }

            @Override
            public Orientation getOrientation() {
               return Orientation.values()[getRequestedOrientation()];
            }

            @Override
            public SimpleOrientation getSimpleOrientation() {
                if (getWindowManager() == null || getWindowManager().getDefaultDisplay() == null) return SimpleOrientation.NA;

                switch (getWindowManager().getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        return SimpleOrientation.PORTRAIT;
                    case Surface.ROTATION_270:
                    case Surface.ROTATION_90:
                        return SimpleOrientation.LANDSCAPE;
                }

                return SimpleOrientation.NA;
            }
        };
        game.vibrationHelper = new VibrationHelper() {
            private Vibrator vibrator;

            @Override
            public void vibrate(long milliseconds) {
                if (!isResumed) return;
                Vibrator vib = getVibrator();
                if (vib == null || !vib.hasVibrator()) return;
                vib.vibrate(milliseconds);
            }

            Vibrator getVibrator() {
                if (vibrator == null) {
                    vibrator = (Vibrator)getContext().getSystemService(VIBRATOR_SERVICE);
                }
                return vibrator;
            }
        };
        game.imageHelper = new ImageHelper() {
            @Override
            public Pixmap getPixmapForIS(InputStream inputStream) {
                Bitmap b = BitmapFactory.decodeStream(inputStream);

                int[] pixels = new int[b.getWidth() * b.getHeight()];
                byte[] toRender = new byte[b.getWidth() * b.getHeight() * 4];
                b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());

                for (int i = 0; i < pixels.length; i++) {
                    int alpha = ((pixels[i] >> 24) & 0x000000FF);
                    int red = ((pixels[i] >> 16) & 0x000000FF);
                    int green = ((pixels[i] >> 8) & 0x000000FF);
                    int blue = ((pixels[i]) & 0x000000FF);
                    toRender[i * 4] = (byte) red;
                    toRender[i * 4 + 1] = (byte) green;
                    toRender[i * 4 + 2] = (byte) blue;
                    toRender[i * 4 + 3] = (byte) alpha;
                }

                Pixmap templatePixmap = new Pixmap(b.getWidth(), b.getHeight(), b.hasAlpha() ? Pixmap.Format.RGBA8888 : Pixmap.Format.RGB888);
                templatePixmap.getPixels().put(toRender).position(0);

                return templatePixmap;
            }
        };
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            try {
                String url = intent.getDataString();
                URI uri = new URI(url);
                if (!uri.getPath().startsWith("/auth")) {
                    game.startupURI = uri;
                }
            } catch (URISyntaxException e) {
            }
        }
        game.captchaRunner = new PxlsGame.CaptchaRunner() {
            @Override
            public void doCaptcha(String token, PxlsGame.CaptchaCallback captchaCallback) {
                Intent intent = new Intent(AndroidLauncher.this, CaptchaActivity.class);
                intent.putExtra("token", token);
                startActivityForResult(intent, CAPTCHA_REQUEST);

                AndroidLauncher.this.captchaCallback = captchaCallback;
            }
        };
        game.loginRunner = new PxlsGame.LoginRunner() {
            @Override
            public void doLogin(String method, String url) {
                if (method.equals("google") || method.equals("discord")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AndroidLauncher.this, LoginActivity.class);
                    intent.putExtra("method", method);
                    intent.putExtra("url", url);
                    startActivityForResult(intent, LOGIN_VIEW);
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 24) {
            game.isMultiWindow = isInMultiWindowMode();
            game.isPIP = isInPictureInPictureMode();
        }

        View view = initializeForView(game, config);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        setContentView(layout);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            try {
                String url = intent.getDataString();
                URI uri = new URI(url);
                if (uri.getPath().startsWith("/auth")) {
                    game.handleAuthenticationCallback(url);
                } else {
                    game.handleView(uri);
                }
            } catch (URISyntaxException e) {
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (Build.VERSION.SDK_INT >= 24 && PxlsGame.i != null) {
            PxlsGame.i.isMultiWindow = isInMultiWindowMode();
            PxlsGame.i.isPIP = isInPictureInPictureMode();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTCHA_REQUEST) {
            if (resultCode == RESULT_OK) {
                String token = data.getStringExtra("token");
                captchaCallback.done(token);
            }
            captchaCallback = null;
        } else if (requestCode == LOGIN_VIEW) {
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("url");
                game.handleAuthenticationCallback(url);
            }
        }
    }
}

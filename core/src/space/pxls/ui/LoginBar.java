package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.JsonObject;
import space.pxls.Pxls;
import space.pxls.PxlsGame;

public class LoginBar extends Table {
    private final Label.LabelStyle ls;

    public LoginBar() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
        ls = new Label.LabelStyle(font, Color.BLACK);

        Label title = new Label("Sign in with...", ls);
        title.setFontScale(0.2f);
        add(title).colspan(3).row();

        addMethod("reddit", "reddit");
        addMethod("google", "Google");
        addMethod("discord", "Discord");
    }

    private void addMethod(final String id, String name) {
        Label reddit = new Label(name, ls);
        reddit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                launchLogin(id);
            }
        });
        reddit.setFontScale(0.4f);
        add(reddit).expandX().uniformX().center();
    }

    public void launchLogin(final String id) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(Pxls.domain + "/signin/" + id);
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                JsonObject jo = Pxls.gson.fromJson(httpResponse.getResultAsString(), JsonObject.class);
                PxlsGame.i.loginRunner.doLogin(id, jo.get("url").getAsString());
            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
    }
}

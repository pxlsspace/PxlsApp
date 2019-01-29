package space.pxls.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.JsonObject;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import java.util.Map;
import java.util.LinkedHashMap;

public class LoginPopup extends Container<Container<Table>> {
    private final Label.LabelStyle ls;
    private boolean up;
    private final Container<Table> container;
    public LoginPopup() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
        ls = new Label.LabelStyle(font, Color.BLACK);
        Table table = new Table();
        
        Map<String, String> services = new LinkedHashMap<String, String>();
        services.put("reddit", "Reddit");
        services.put("google", "Google");
        services.put("discord", "Discord");
        services.put("vk", "VK");
        services.put("tumblr", "Tumblr");
        
        for (final String url : services.keySet()) {
            String display = services.get(url);
            Label title = new Label(display, ls);
            title.setFontScale(0.4f);
            title.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    popDown();
                    launchLogin(url);
                }
            });
            table.add(title).row();
        }
        
    
        container = new Container<Table>(table);
        container.setBackground(Pxls.skin.getDrawable("background"));
        container.pad(8);
        setActor(container);
        setClip(true);
    }

    public void toggle() {
        if (up) {
            popDown();
        } else {
            popUp();
        }
    }

    public void popUp() {
        up = true;

        MoveToAction mta = new MoveToAction();
        mta.setPosition(0, 0);
        mta.setDuration(0.2f);
        mta.setInterpolation(Interpolation.exp5Out);

        container.addAction(mta);
    }

    public void popDown() {
        up = false;

        MoveToAction mta = new MoveToAction();
        mta.setPosition(0, -container.getHeight());
        mta.setDuration(0.2f);
        mta.setInterpolation(Interpolation.exp5In);

        container.addAction(mta);
    }

    private void launchLogin(final String id) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(Pxls.domain + "/signin/" + id);
        req.setHeader("User-Agent", Pxls.getUA());
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

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!up && !container.hasActions()) {
            container.setY(-container.getHeight());
        }
    }
}

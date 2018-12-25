package space.pxls;

import com.google.gson.JsonObject;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import space.pxls.ui.LoadScreen;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PxlsClient {
    private UpdateCallback updateCallback;
    private WebSocketClient client;
    private PendingPixel pendingPixel;
    private Account account;
    public boolean loggedIn;

    public PxlsClient(final UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
        try {
            loggedIn = false;
            String wsPath = Pxls.wsPath;

            Map<String, String> headers = new HashMap<String, String>();
            if (Pxls.prefsHelper.getToken() != null) {
                headers.put("Cookie", "pxls-token=" + Pxls.prefsHelper.getToken());
            }
            client = new WebSocketClient(new URI(wsPath), new Draft_17(), headers, 30) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                }

                @Override
                public void onMessage(String message) {
                    JsonObject obj = Pxls.gson.fromJson(message, JsonObject.class);
                    String type = obj.get("type").getAsString();
                    if (type.equals("pixel")) {
                        PixelsPacket pixelsPacket = Pxls.gson.fromJson(message, PixelsPacket.class);
                        for (PixelsPacket.Pixel pixel : pixelsPacket.pixels) {
                            updateCallback.pixel(pixel.x, pixel.y, pixel.color);
                        }
                    } else if (type.equals("users")) {
                        UsersPacket usersPacket = Pxls.gson.fromJson(message, UsersPacket.class);
                        updateCallback.users(usersPacket.users);
                    } else if (type.equals("userinfo")) {
                        UserInfoPacket userInfoPacket = Pxls.gson.fromJson(message, UserInfoPacket.class);
                        loggedIn = true;
                        account = new Account(userInfoPacket.username, userInfoPacket.banned, userInfoPacket.role.equals("BANNED") ? 0 : userInfoPacket.banExpiry, userInfoPacket.ban_reason);
                        updateCallback.updateAccount(account);
                    } else if (type.equals("cooldown")) {
                        CooldownPacket cooldownPacket = Pxls.gson.fromJson(message, CooldownPacket.class);
                        updateCallback.cooldown(cooldownPacket.wait);
                    } else if (type.equals("captcha_required")) {
                        updateCallback.runCaptcha();
                    } else if (type.equals("captcha_status")) {
                        boolean success = obj.get("success").getAsBoolean();
                        if (success) {
                            placePixel(pendingPixel.x, pendingPixel.y, pendingPixel.color);
                        } else {
                            final GDXButtonDialog gdxButtonDialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
                            gdxButtonDialog.setTitle("pxls.space");
                            gdxButtonDialog.setMessage("Failed captcha verification");
                            gdxButtonDialog.addButton(":(");
                            gdxButtonDialog.setClickListener(new ButtonClickListener() {
                                @Override
                                public void click(int button) {
                                    gdxButtonDialog.dismiss();
                                }
                            });
                            gdxButtonDialog.build().show();
                        }
                        pendingPixel = null;
                    } else if (type.equals("alert")) {
                        AlertPacket msg = Pxls.gson.fromJson(message, AlertPacket.class);

                        final GDXButtonDialog gdxButtonDialog = Pxls.dialogs.newDialog(GDXButtonDialog.class);
                        gdxButtonDialog.setTitle("pxls.space");
                        gdxButtonDialog.setMessage(msg.message);
                        gdxButtonDialog.addButton("OK");
                        gdxButtonDialog.setClickListener(new ButtonClickListener() {
                            @Override
                            public void click(int button) {
                                gdxButtonDialog.dismiss();
                            }
                        });
                        gdxButtonDialog.build().show();
                    } else if (type.equals("can_undo")) {
                        CanUndoPacket p = Pxls.gson.fromJson(message, CanUndoPacket.class);
                        updateCallback.canUndo(p.time);
                    } else if (type.equals("pixels")) {
                        PixelsStackPacket p = Pxls.gson.fromJson(message, PixelsStackPacket.class);
                        updateCallback.stack(p.count, p.cause);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (remote) {
                        PxlsGame.i.setScreen(new LoadScreen());
                    } else {
                        client.connect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                }
            };

            if (wsPath.startsWith("wss://")) {
                SSLContext sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }
                }}, new SecureRandom());
                client.setSocket(sslContext.getSocketFactory().createSocket(client.getURI().getHost(), 443));
            }

            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        client.close();
    }

    public void finishCaptcha(String token) {
        client.send(Pxls.gson.toJson(new CaptchaResult(token)));
    }

    public void placePixel(int x, int y, int color) {
        client.send(Pxls.gson.toJson(new PixelPacket(x, y, color)));
        pendingPixel = new PendingPixel(x, y, color);
    }

    public void undo() {
        client.send(Pxls.gson.toJson(new UndoPacket()));
    }

    public interface UpdateCallback {
        void pixel(int x, int y, int color);

        void users(int users);

        void updateAccount(Account account);

        void cooldown(float seconds);

        void canUndo(float seconds);

        void stack(int count, String cause);

        void runCaptcha();
    }

    static class PixelsPacket {
        List<Pixel> pixels;

        static class Pixel {
            int x;
            int y;
            int color;
        }
    }

    static class UsersPacket {
        int users;
    }

    static class UserInfoPacket {
        String username;
        boolean banned;
        String ban_reason;
        String role;
        long banExpiry;
    }

    static class CooldownPacket {
        float wait;
    }

    static class PixelPacket {
        String type = "place";
        int x;
        int y;
        int color;

        public PixelPacket(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    static class CaptchaResult {
        String type = "captcha";
        String token;

        public CaptchaResult(String token) {
            this.token = token;
        }
    }

    static class PendingPixel {
        int x;
        int y;
        int color;

        public PendingPixel(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    static class AlertPacket {
        String message;
    }

    static class CanUndoPacket {
        float time;
    }

    static class UndoPacket {
        String type = "undo";
    }

    static class PixelsStackPacket {
        int count;
        String cause;
    }
}

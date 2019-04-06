package space.pxls.ui.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import space.pxls.Pxls;
import space.pxls.Skin;

public class TitledTableHelper extends Table {
    public TitledTableHelper(String title) {
        setBackground(new NinePatchDrawable(Pxls.skin.getPatch("white-patch"))); //white-patch is a simple bordered 9patch with white background
        Label label = new TTFLabel(title, Skin.DP_TITLE);
//        label.getStyle().fontColor = new Color(1f, 1f, 1f, 1f);
        label.setAlignment(Align.center);
        add(new Stack(new SolidContainer(new Color(0, 0, 0, 0.1f)), label)).colspan(2).fillX().center().row(); //actual title
        add(new SolidContainer()).height(6).colspan(2).padBottom(3).fillX().row(); //bottom "border"
    }
}

package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

import space.pxls.renderers.Canvas;
import space.pxls.renderers.Template;
import space.pxls.renderers.Virginmap;
import space.pxls.ui.CanvasScreen;

public class PxlsGameState {
    public CanvasState canvasState = new CanvasState();
    public TemplateState templateState = new TemplateState();
    public HeatmapState heatmapState = new HeatmapState();
    public GridState gridState = new GridState();
    public VirginmapState virginmapState = new VirginmapState();

    public PxlsGameState() {}
    public PxlsGameState(CanvasState canvasState, TemplateState templateState, HeatmapState heatmapState, GridState gridState, VirginmapState virginmapState) {
        this.canvasState = canvasState;
        this.templateState = templateState;
        this.heatmapState = heatmapState;
        this.gridState = gridState;
        this.virginmapState = virginmapState;
    }

    public void serialize(Map<String, ?> prefs) {
        if (prefs.get(TemplateState.ConfigKeys.offsetX) != null) {
            //assume we can serialize template
            this.templateState.offsetX = (Integer) prefs.get(TemplateState.ConfigKeys.offsetX);
            this.templateState.offsetY = (Integer) prefs.get(TemplateState.ConfigKeys.offsetY);
            this.templateState.totalWidth = (Float) prefs.get(TemplateState.ConfigKeys.totalWidth);
            this.templateState.URL = (String) prefs.get(TemplateState.ConfigKeys.URL);
            this.templateState.opacity = (Float) prefs.get(TemplateState.ConfigKeys.opacity);
        }
        if (prefs.get(HeatmapState.ConfigKeys.opacity) != null) {
            //assume we can serialize heatmap
            if (this.heatmapState == null) this.heatmapState = new HeatmapState();
            this.heatmapState.opacity = (Float) prefs.get(HeatmapState.ConfigKeys.opacity);
        }
        if (prefs.get(CanvasState.ConfigKeys.panX) != null) {
            //assume we can serialize canvas
            if (this.canvasState == null) this.canvasState = new CanvasState();
            this.canvasState.panX = (Integer) prefs.get(CanvasState.ConfigKeys.panX);
            this.canvasState.panY = (Integer) prefs.get(CanvasState.ConfigKeys.panY);
            this.canvasState.zoom = (Float) prefs.get(CanvasState.ConfigKeys.zoom);
        }
        if (prefs.get(VirginmapState.ConfigKeys.opacity) != null) {
            if (this.virginmapState == null) this.virginmapState = new VirginmapState();
            this.virginmapState.opacity = (Float) prefs.get(VirginmapState.ConfigKeys.opacity);
        }
    }
    public Map<String, ?> deserialize() {
        Map<String, Object> toRet = new HashMap<String, Object>();

        if (this.canvasState != null) {
            toRet.putAll(this.canvasState.deserialize());
        } else {
            toRet.putAll(CanvasState.DefaultValuesMap);
        }

        if (this.templateState != null) {
            toRet.putAll(this.templateState.deserialize());
        } else {
            toRet.putAll(TemplateState.DefaultValuesMap);
        }

        if (this.heatmapState != null) {
            toRet.putAll(this.heatmapState.deserialize());
        } else {
            toRet.putAll(HeatmapState.DefaultValuesMap);
        }

        if (this.virginmapState != null) {
            toRet.putAll(this.virginmapState.deserialize());
        } else {
            toRet.putAll(VirginmapState.DefaultValuesMap);
        }

        return toRet;
    }

    public CanvasState getSafeCanvasState() {
        if (this.canvasState == null) this.canvasState = new CanvasState();
        return this.canvasState;
    }

    public HeatmapState getSafeHeatmapState() {
        if (this.heatmapState == null) this.heatmapState = new HeatmapState();
        return this.heatmapState;
    }

    public GridState getSafeGridState() {
        if (this.gridState == null) this.gridState = new GridState();
        return this.gridState;
    }

    public TemplateState getSafeTemplateState() {
        if (this.templateState == null) this.templateState = new TemplateState();
        return this.templateState;
    }

    public VirginmapState getSafeVirginmapState() {
        if (this.virginmapState == null) this.virginmapState = new VirginmapState();
        return this.virginmapState;
    }

    public PxlsGameState setCanavsLocation(CanvasState canvasState) {
        this.canvasState = canvasState;
        return this;
    }

    public PxlsGameState setTemplateState(TemplateState templateState) {
        this.templateState = templateState;
        return this;
    }

    public PxlsGameState setHeatmapState(HeatmapState heatmapState) {
        this.heatmapState = heatmapState;
        return this;
    }

    public void setVirginmapState(VirginmapState virginmapState) {
        this.virginmapState = virginmapState;
    }

    @Override
    public String toString() {
        return String.format("GameState {canvasState: {%s}; TemplateState: {%s}; HeatmapState: {%s}; VirginmapState: {%s}; }", this.canvasState.toString(), this.templateState.toString(), this.heatmapState.toString(), this.virginmapState.toString());
    }
}

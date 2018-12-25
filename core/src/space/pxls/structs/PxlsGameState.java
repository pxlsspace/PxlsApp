package space.pxls.structs;

import java.util.HashMap;
import java.util.Map;

public class PxlsGameState {
    public CanvasState canvasState = new CanvasState();
    public TemplateState templateState = new TemplateState();
    public HeatmapState heatmapState = new HeatmapState();
    public GridState gridState = new GridState();

    public PxlsGameState() {}
    public PxlsGameState(CanvasState canvasState, TemplateState templateState, HeatmapState heatmapState, GridState gridState) {
        this.canvasState = canvasState;
        this.templateState = templateState;
        this.heatmapState = heatmapState;
        this.gridState = gridState;
    }

    public void serialize(Map<String, ?> prefs) {
        if (prefs.get(TemplateState.ConfigKeys.offsetX) != null) {
            //assume we can serialize template
            this.templateState.offsetX = (Integer) prefs.get(TemplateState.ConfigKeys.offsetX);
            this.templateState.offsetY = (Integer) prefs.get(TemplateState.ConfigKeys.offsetY);
            this.templateState.totalWidth = (Float) prefs.get(TemplateState.ConfigKeys.totalWidth);
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

    @Override
    public String toString() {
        return String.format("GameState {canvasState: {%s}; TemplateState: {%s}; HeatmapState: {%s};}", this.canvasState.toString(), this.templateState.toString(), this.heatmapState.toString());
    }
}

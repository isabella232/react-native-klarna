package com.rnklarna;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.common.MapBuilder;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KlarnaViewManager extends SimpleViewManager<KlarnaView> {

  public static final String REACT_CLASS = "RNKlarna";

  @Nonnull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Nonnull
  @Override
  protected KlarnaView createViewInstance(@Nonnull ThemedReactContext reactContext) {
    return new KlarnaView(reactContext);
  }

  @ReactProp(name = "snippet")
  public void setSnippet(KlarnaView view, @Nullable String snippet) {
    view.setSnippet(snippet);
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
    MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
    return builder.put("onComplete",
            MapBuilder.of("phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onComplete")))
            .build();
  }

}
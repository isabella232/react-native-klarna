package com.rnklarna;

import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.common.MapBuilder;

import android.view.ViewGroup;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Map;

public class KlarnaViewManager extends ViewGroupManager<LinearLayout> {

  private KlarnaView klarnaView;
  public static final String REACT_CLASS = "RNKlarna";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected LinearLayout createViewInstance(ThemedReactContext themedReactContext) {
    // Creating a wrapper instead of a view is needed for correct work of KLarnaCheckout.destroy()
    klarnaView = new KlarnaView(themedReactContext);

    // The following hierarchy is required for Klarna to work properly:
    //
    // <LinearLayout /*container*/> - to wrap the scroll view
    // ..<ScrollView /*scrollView*/> - because KlarnaView itself is not scrollable
    // ....<FrameLayout /*contentView*/> - needed otherwise KlarnaView doesn't resize properly
    // ......<KlarnaView /*view*/> - KlarnaView itself
    LinearLayout container = new LinearLayout(themedReactContext);

    ScrollView scrollView = new ScrollView(themedReactContext);
    scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    container.addView(scrollView);

    FrameLayout contentView = new FrameLayout(themedReactContext);
    contentView.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
    scrollView.addView(contentView);

    final View view = klarnaView.getmView();
    contentView.addView(view);

    return container;
  }

  @ReactProp(name = "snippet")
  public void setSnippet(View view, String snippet) {
    klarnaView.setSnippet(snippet);
  }


  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder()
            .put(
                    "onComplete",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onComplete")))
            .build();
  }


}
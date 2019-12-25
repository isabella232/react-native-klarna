package com.rnklarna;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import com.klarna.checkout.KlarnaCheckout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KlarnaView extends FrameLayout {

  private final @Nonnull ThemedReactContext appContext;
  private final @Nonnull FrameLayout mContainerView;
  private final @Nonnull ScrollView mScrollView;

  private @Nullable KlarnaCheckout mCheckout;
  private @Nullable String mSnippet;

  public KlarnaView(@Nonnull ThemedReactContext themedReactContext) {
    super(themedReactContext);
    this.appContext = themedReactContext;

    ScrollView scrollView = new ScrollView(themedReactContext);
    mScrollView = scrollView;
    scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    addView(scrollView);

    FrameLayout containerView = new FrameLayout(themedReactContext);
    mContainerView = containerView;
    containerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    scrollView.addView(containerView);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (mCheckout != null) {
      return;
    }
    Activity activity = this.appContext.getCurrentActivity();
    if (activity == null) {
      return;
    }
    KlarnaCheckout checkout = new KlarnaCheckout(activity, getReturnURL());
    checkout.setSnippet(mSnippet);
    checkout.setSignalListener((eventName, jsonObject) -> onReceiveNativeEvent(jsonObject, eventName));
    View klarnaView = checkout.getView();
    if (klarnaView == null) {
      return;
    }
    mContainerView.addView(checkout.getView());
    mCheckout = checkout;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mCheckout == null) {
      return;
    }
    mContainerView.removeAllViews();
    mCheckout.destroy();
    mCheckout = null;
  }

  public void onReceiveNativeEvent(JSONObject jsonObject, String eventName) {
    appContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            getId(),
            "onComplete",
            serializeEventData(jsonObject, eventName, getId())
    );
  }

  public void setSnippet(@Nullable String snippet) {
    this.mSnippet = snippet;
    updateSnippet();
  }

  private String getReturnURL() {
    int resId = appContext.getResources().getIdentifier(
            "return_url_klarna",
            "string",
            appContext.getPackageName()
    );
    String returnURL;
    try {
      returnURL = appContext.getString(resId);
    } catch (Resources.NotFoundException e) {
      returnURL = appContext.getPackageName();
    }
    return returnURL;
  }

  private void updateSnippet() {
    if (mCheckout == null) {
      return;
    }
    if (mSnippet != null && mSnippet.equals("error")) {
      mCheckout.destroy();
    } else {
      mCheckout.setSnippet(mSnippet);
    }
    mScrollView.scrollTo(0, 0);
  }

  public static WritableMap serializeEventData(JSONObject jsonObject, String eventName, int id) {
    WritableMap event = Arguments.createMap();
    WritableMap data = Arguments.createMap();
    try {
      data = jsonToReact(jsonObject);
    } catch (JSONException e) {
      Log.e(e.getMessage(), e.toString());
    }
    event.putString("type", "onComplete");
    event.putMap("data", data);
    event.putInt("target", id);
    event.putString("signalType", eventName);
    return event;
  }

  private static WritableMap jsonToReact(JSONObject jsonObject) throws JSONException {
    WritableMap writableMap = Arguments.createMap();
    Iterator iterator = jsonObject.keys();
    while(iterator.hasNext()) {
      String key = (String) iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof Float || value instanceof Double) {
        writableMap.putDouble(key, jsonObject.getDouble(key));
      } else if (value instanceof Number) {
        writableMap.putInt(key, jsonObject.getInt(key));
      } else if (value instanceof String) {
        writableMap.putString(key, jsonObject.getString(key));
      } else if (value instanceof JSONObject) {
        writableMap.putMap(key,jsonToReact(jsonObject.getJSONObject(key)));
      } else if (value instanceof JSONArray){
        writableMap.putArray(key, jsonToReact(jsonObject.getJSONArray(key)));
      } else if (value == JSONObject.NULL){
        writableMap.putNull(key);
      }
    }

    return writableMap;
  }
  private static WritableArray jsonToReact(JSONArray jsonArray) throws JSONException {
    WritableArray writableArray = Arguments.createArray();
    for(int i=0; i < jsonArray.length(); i++) {
      Object value = jsonArray.get(i);
      if (value instanceof Float || value instanceof Double) {
        writableArray.pushDouble(jsonArray.getDouble(i));
      } else if (value instanceof Number) {
        writableArray.pushInt(jsonArray.getInt(i));
      } else if (value instanceof String) {
        writableArray.pushString(jsonArray.getString(i));
      } else if (value instanceof JSONObject) {
        writableArray.pushMap(jsonToReact(jsonArray.getJSONObject(i)));
      } else if (value instanceof JSONArray){
        writableArray.pushArray(jsonToReact(jsonArray.getJSONArray(i)));
      } else if (value == JSONObject.NULL){
        writableArray.pushNull();
      }
    }
    return writableArray;
  }

}

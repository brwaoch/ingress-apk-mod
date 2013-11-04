package com.nianticproject.ingress.common.ui;

import com.badlogic.gdx.InputMultiplexer;

public abstract class BaseSubActivity extends InputMultiplexer implements SubActivity {

    public BaseSubActivity(String s) {
    }

    protected UiRenderer getRenderer() {
        return null;
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected abstract String getName();
}

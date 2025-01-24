package com.alp.ir.viradictionary;

import android.app.Application;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class ViraApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath("fonts/iranyekan.ttf")
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build());
        }
    }
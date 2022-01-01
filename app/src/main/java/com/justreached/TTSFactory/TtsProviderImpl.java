package com.justreached.TTSFactory;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Syed.Zakriya on 22-06-2017.
 */

public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    public void init(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }

    @Override
    public void say(String sayThis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null,null);
        }
        else
        {
            tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        Locale loc = new Locale("en", "", "");
        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        }
    }

    public void shutdown() {
        tts.shutdown();
    }}
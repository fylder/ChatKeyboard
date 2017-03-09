package fylder.chat.keyboard;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import fylder.keyboard.lib.bean.EmoticonEntity;
import fylder.keyboard.lib.utils.EmoticonBase;
import fylder.keyboard.lib.utils.EmotionInit;

/**
 * Created by fylder on 2017/3/8.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EmotionInit.isEmoticonInitSuccess(this)) {
            List<EmoticonEntity> entities = new ArrayList<>();
            entities.add(new EmoticonEntity("emoticons/xhs", EmoticonBase.Scheme.ASSETS));
            entities.add(new EmoticonEntity("emoticons/tusiji", EmoticonBase.Scheme.ASSETS));
            EmotionInit.initEmoticonsDB(this, true, entities);
        }
    }
}

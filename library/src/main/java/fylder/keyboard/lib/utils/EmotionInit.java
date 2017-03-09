package fylder.keyboard.lib.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fylder.keyboard.lib.bean.EmoticonBean;
import fylder.keyboard.lib.bean.EmoticonEntity;
import fylder.keyboard.lib.bean.EmoticonSetBean;
import fylder.keyboard.lib.db.EmoticonDBHelper;

/**
 * Created by fylder on 2017/3/8.
 */

public class EmotionInit {

    public static boolean isEmoticonInitSuccess(Context context) {
        return Utils.isInitDb(context);
    }

    public static void initEmoticonsDB(final Context context, final boolean isShowEmoji, final List<EmoticonEntity> emoticonEntities) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                EmoticonDBHelper emoticonDbHelper = EmoticonHandler.getInstance(context).getEmoticonDbHelper();
                if (isShowEmoji) {
                    ArrayList<EmoticonBean> emojiArray = Utils.ParseData(DefEmoticons.emoticonArray, EmoticonBean.FACE_TYPE_NORMAL, EmoticonBase.Scheme.DRAWABLE);
                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                    emojiEmoticonSetBean.setIconUri("drawable://f071");//表情显示的资源路径
                    emojiEmoticonSetBean.setItemPadding(25);
                    emojiEmoticonSetBean.setVerticalSpacing(10);
                    emojiEmoticonSetBean.setShowDelBtn(true);
                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
                    emoticonDbHelper.insertEmoticonSet(emojiEmoticonSetBean);
                }

                List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();
                for (EmoticonEntity entity : emoticonEntities) {
                    try {
                        EmoticonSetBean bean = Utils.ParseEmoticons(context, entity.getPath(), entity.getScheme());
                        emoticonSetBeans.add(bean);
                    } catch (IOException e) {
                        e.printStackTrace();
                        HadLog.e(String.format("read %s config.xml error", entity.getPath()));
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        HadLog.e(String.format("parse %s config.xml error", entity.getPath()));
                    }
                }

                for (EmoticonSetBean setBean : emoticonSetBeans) {
                    emoticonDbHelper.insertEmoticonSet(setBean);
                }
                emoticonDbHelper.cleanup();

                if (emoticonSetBeans.size() == emoticonEntities.size()) {
                    Utils.setIsInitDb(context, true);
                }
                return null;
            }
        }.execute();

    }
}

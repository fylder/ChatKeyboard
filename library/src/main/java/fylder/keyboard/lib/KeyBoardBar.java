package fylder.keyboard.lib;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import fylder.keyboard.lib.bean.EmoticonBean;
import fylder.keyboard.lib.bean.EmoticonSetBean;
import fylder.keyboard.lib.db.EmoticonDBHelper;
import fylder.keyboard.lib.utils.DisplayUtils;
import fylder.keyboard.lib.utils.EmoticonHandler;
import fylder.keyboard.lib.utils.EmoticonsKeyboardBuilder;
import fylder.keyboard.lib.utils.TimeTools;
import fylder.keyboard.lib.utils.Utils;
import fylder.keyboard.lib.utils.VoiceUtils;
import fylder.keyboard.lib.view.EmoticonLayout;
import fylder.keyboard.lib.view.EmoticonsIndicatorView;
import fylder.keyboard.lib.view.EmoticonsPageView;
import fylder.keyboard.lib.view.EmoticonsToolBarView;
import fylder.keyboard.lib.view.HadEditText;
import fylder.keyboard.lib.view.RecordingView;
import fylder.keyboard.lib.view.SoftHandleLayout;

/**
 * chat demo
 * Created by fylder on 2015/8/25.
 */
public class KeyBoardBar extends SoftHandleLayout implements View.OnClickListener, EmoticonsToolBarView.OnToolBarItemClickListener {

    String TAG = KeyBoardBar.class.getName();

    public static final int FUNC_EMOTICON = 1;
    public static final int FUNC_MORE = 2;
    public static final int FUNC_VOICE = 3;
    public int mChildViewPosition = -1; //显示哪个子视图

    public static final int STATE_EMOTICON = 200;
    public static final int STATE_MORE = 201;
    public static final int STATE_VOICE = 202;
    public static final int STATE_EMTY = 400;

    protected int mState = STATE_EMTY;

    RelativeLayout footerLay;//显示内容
    RelativeLayout msgLay;
    HadEditText msgEdit;//信息输入
    RelativeLayout msgDefaultLay;
    ImageView actionImg;
    ImageView voiceImg;
    ImageView moreImg;
    ImageView emotionImg;

    TextView sendBtn;


    private Context mContext;

    private boolean mIsMultimediaVisibility = true;

    int TYPE = 1;
    int MORE_FLAG = 1;
    int SEND_FLAG = 2;

    public KeyBoardBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.keyboard_lay, this);
        initView();

    }

    private void initView() {
        msgLay = (RelativeLayout) findViewById(R.id.keyboard_msg_lay);
        msgEdit = (HadEditText) findViewById(R.id.keyboard_msg);
        msgDefaultLay = (RelativeLayout) findViewById(R.id.keyboard_msg_default_lay);
        actionImg = (ImageView) findViewById(R.id.keyboard_msg_default_up_down);
        voiceImg = (ImageView) findViewById(R.id.keyboard_voice);
        emotionImg = (ImageView) findViewById(R.id.keyboard_emotion);
        moreImg = (ImageView) findViewById(R.id.keyboard_more);
        sendBtn = (TextView) findViewById(R.id.keyboard_send_btn);
        footerLay = (RelativeLayout) findViewById(R.id.keyboard_footer_lay);

        msgDefaultLay.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        voiceImg.setOnClickListener(this);
        emotionImg.setOnClickListener(this);

        setAutoHeightLayoutView(footerLay);
        initEdit();
        init();
    }

    private void init() {
//        EmoticonHandler.getInstance(mContext).loadEmoticonsToMemory();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View emotionView = inflater.inflate(R.layout.chat_emotion_lay, null);
//        add(emotionView);
        showEmoticons();

        View moreView = inflater.inflate(R.layout.chat_more_lay, null);
        initMoreLay(moreView);
        add(moreView);

        View voiceView = inflater.inflate(R.layout.chat_voice_lay, null);
        initRecordView(voiceView);
        add(voiceView);
    }

    boolean isVoice = false;
    boolean isVoiceShow = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.keyboard_more) {
            mState = STATE_MORE;
            show(FUNC_MORE);
            showAutoView();
            Utils.closeSoftKeyboard(mContext);
            if (isVoice) {
                voiceImg.setImageResource(R.drawable.voice);
                msgDefaultLay.setVisibility(GONE);
                msgLay.setVisibility(VISIBLE);
                setEditableState(false);
                isVoice = false;
            }
            emotionImg.setImageResource(R.mipmap.chatroom_input_icon_face);
        } else if (id == R.id.keyboard_send_btn) {

            //发送
            String msg = msgEdit.getText().toString().trim();
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.onKeyBoardSendMsg(msg);
                msgEdit.setText("");
            }
        } else if (id == R.id.keyboard_voice) {

            mState = STATE_VOICE;
            show(FUNC_VOICE);
            showAutoView();
            Utils.closeSoftKeyboard(mContext);
            //切换显示
            if (!isVoice) {
                //显示voice时
                voiceImg.setImageResource(R.drawable.keyboard_selector);
                msgDefaultLay.setVisibility(VISIBLE);
                msgLay.setVisibility(GONE);
                isVoice = true;
                isVoiceShow = true;
            } else {
                voiceImg.setImageResource(R.drawable.voice);
                msgDefaultLay.setVisibility(GONE);
                msgLay.setVisibility(VISIBLE);
                setEditableState(true);
                Utils.openSoftKeyboard(msgEdit);
                isVoice = false;
            }
            emotionImg.setImageResource(R.mipmap.chatroom_input_icon_face);
        } else if (id == R.id.keyboard_emotion) {
            mState = STATE_EMOTICON;
            show(FUNC_EMOTICON);
            showAutoView();
            Utils.closeSoftKeyboard(mContext);
            emotionImg.setImageResource(R.mipmap.chatroom_input_icon_face_press);
        } else if (id == R.id.keyboard_msg_default_lay) {
            if (isVoiceShow) {
                hideAutoView();
                actionImg.setImageResource(R.mipmap.keyboard_action_up);
                isVoiceShow = false;
            } else {
                showAutoView();
                actionImg.setImageResource(R.mipmap.keyboard_action_down);
                isVoiceShow = true;
            }

        }
    }

    /**
     * 输入框
     */
    private void initEdit() {

        msgEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!msgEdit.isFocused()) {
                    msgEdit.setFocusable(true);
                    msgEdit.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        msgEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
//        msgEdit.setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
//            @Override
//            public void onSizeChanged() {
//                post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mKeyBoardBarViewListener != null) {
//                            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
//                        }
//                    }
//                });
//            }
//        });
        msgEdit.setOnTextChangedInterface(new HadEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                String str = arg0.toString();
                if (TextUtils.isEmpty(str)) {
                    if (mIsMultimediaVisibility) {
                        sendBtn.setVisibility(GONE);
                        moreImg.setVisibility(VISIBLE);
                    } else {
                        sendBtn.setBackgroundResource(R.drawable.btn_send_bg_disable);
                    }
                }
                // -> 发送
                else {
                    if (mIsMultimediaVisibility) {
                        sendBtn.setVisibility(VISIBLE);
                        moreImg.setVisibility(GONE);
                        sendBtn.setBackgroundResource(R.drawable.send_btn);
                    } else {
                        sendBtn.setBackgroundResource(R.drawable.send_btn);
                    }
                }
            }
        });
    }

    /**
     * 设置输入框的状态
     *
     * @param b
     */
    private void setEditableState(boolean b) {
        if (b) {
            msgEdit.setFocusable(true);
            msgEdit.setFocusableInTouchMode(true);
            msgEdit.requestFocus();
            msgLay.setBackgroundResource(R.drawable.chat_bg_focus);
        } else {
            msgEdit.setFocusable(false);
            msgEdit.setFocusableInTouchMode(false);
            msgLay.setBackgroundResource(R.drawable.chat_bg);
        }
    }


    /**
     * 添加子布局
     *
     * @param view
     */
    public void add(View view) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        footerLay.addView(view, params);
    }


    public void showEmoticons() {
//        btnEmoticon.setVisibility(VISIBLE);
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setContents(builder, new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (msgEdit != null) {
                    msgEdit.setFocusable(true);
                    msgEdit.setFocusableInTouchMode(true);
                    msgEdit.requestFocus();

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        msgEdit.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        if (mKeyBoardBarViewListener != null) {
                            Log.w("fylder","tag:"+bean.getTag()+"\turi:"+bean.getIconUri());
//                            mKeyBoardBarViewListener.onUserDefEmoticonClicked(bean.getTag(), bean.getIconUri()); //暂不处理
                        }
                        return;
                    }

                    int index = msgEdit.getSelectionStart();
                    Editable editable = msgEdit.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getTag());
                    } else {
                        editable.insert(index, bean.getTag());
                    }
                }
            }
        });
        add(layout);
//        FUNC_EMOTICON_POS = FUNC_ORDER_COUNT;
//        ++FUNC_ORDER_COUNT;
    }

    private EmoticonsKeyboardBuilder getBuilder(Context context) {
        if (context == null) {
            throw new RuntimeException(" Context is null, cannot create db helper");
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder().setEmoticonSetBeanList(mEmoticonSetBeanList).build();
    }

    /**
     * 显示那块布局
     *
     * @param position
     */
    public void show(int position) {

        int childCount = footerLay.getChildCount();
        if (position < childCount) {
            for (int i = 0; i < childCount; i++) {
                if (i == position) {
                    footerLay.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition = i;
                } else {
                    footerLay.getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (footerLay != null && footerLay.isShown()) {
                    hideAutoView();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void OnSoftKeyboardPop(int height) {
        super.OnSoftKeyboardPop(height);
        mState = STATE_EMTY;//恢复状态
    }

    @Override
    public void OnSoftKeyboardClose() {
        super.OnSoftKeyboardClose();
        if (mState == STATE_MORE) {//弹出更多，键盘消失
            mState = STATE_EMTY;//恢复状态
        } else if (mState == STATE_VOICE) {
            mState = STATE_EMTY;
        } else if (mState == STATE_EMOTICON) {
            mState = STATE_EMOTICON;
        } else {
            hideAutoView();
        }
    }

    //*******Record**********
    RecordingView recordingView;
    Handler mHandler;
    ImageView recordImg;
    TextView recordTimeText;
    TextView recordTipText;

    long StartTime;
    long RecordStartTime;
    long RecordStopTime;

    boolean ISCANCEL = false;

    /**
     * 录音效果
     *
     * @param view
     */
    private void initRecordView(View view) {
        recordingView = (RecordingView) view.findViewById(R.id.chat_voice_record);
        recordImg = (ImageView) view.findViewById(R.id.chat_voice_img);
        recordTimeText = (TextView) view.findViewById(R.id.chat_voice_time);
        recordTipText = (TextView) view.findViewById(R.id.chat_voice_tip);


        recordImg.setOnTouchListener(new OnTouchListener() {

            float x, y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    x = event.getX();
                    y = event.getY();

                    StartTime = new Date().getTime();
                    VoiceUtils.getInstance().startRecord();//开始录音
                    recordShow();//录音动画效果
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    String audioPath = VoiceUtils.getInstance().stopRecord();//停止录音
                    recordTipText.setText("按住说话");
                    RecordStopTime = new Date().getTime();
                    isRecording = false;
                    long t = RecordStopTime - RecordStartTime;
                    if (t >= 1000 && !audioPath.equals("")) {
                        if (ISCANCEL) {
                            Toast.makeText(mContext, "取消录音", Toast.LENGTH_SHORT).show();
                            //mKeyBoardBarViewListener.onKeyBoardVoice("取消录音");
                        } else {
                            mKeyBoardBarViewListener.onKeyBoardVoice(audioPath, (int) (t / 1000));
                        }
                    } else if (500 < t && t < 1000 && !audioPath.equals("")) {
                        Toast.makeText(mContext, "录音不到一秒", Toast.LENGTH_SHORT).show();
                    } else if (audioPath.equals("")) {
                        Toast.makeText(mContext, "录音有误", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "计算时间有误");
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    int radius = 40;//40dp的范围

                    float x1, y1;
                    x1 = event.getX();
                    y1 = event.getY();
                    if (Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2) > Math.pow(DisplayUtils.dp2px(mContext, radius), 2)) {
                        //超出范围
                        ISCANCEL = true;
                    } else {
                        ISCANCEL = false;
                    }

                }
                return true;
            }
        });
        mHandler = new Handler(Looper.getMainLooper());
    }

    private boolean isRecording = true;
    private MediaRecorder mMediaRecorder;

    /**
     * 录音动态显示
     * <p/>
     * 50ms refresh
     * <p/>
     * 在run里注意mMediaRecorder的取值，
     */
    private void recordShow() {
        recordTipText.setText("划开取消");
        RecordStartTime = new Date().getTime();

        isRecording = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMediaRecorder = VoiceUtils.getInstance().getmMediaRecorder();
                if (mMediaRecorder != null) {

                    String rT = TimeTools.getRecordTime(RecordStartTime);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = rT;
                    handler.sendMessage(message);//更新录音时间

                    int i = mMediaRecorder.getMaxAmplitude();
                    float radius = (float) Math.log10(Math.max(1, i - 500)) * DisplayUtils.dp2px(mContext, 20);
                    recordingView.animateRadius(radius);
                    if (isRecording) {
                        mHandler.postDelayed(this, RecordingView.DTIME);
                    }
                } else {
                    recordingView.animateRadius(0);
                }
            }
        });

    }


    private void initMoreLay(View view) {
        view.findViewById(R.id.item_chat_more_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.onKeyBoardPhoto();
                }

            }
        });
        view.findViewById(R.id.item_chat_more_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.onKeyBoardCamera();
                }
            }
        });

    }

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;

    /**
     * 表情初始化
     *
     * @param view
     */


    /**
     * 刷新时间UI
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                // 更新时间
                String rT = (String) msg.obj;
                recordTimeText.setText(rT);
            } else if (msg.what == 2) {
                // 重置时间
                recordTimeText.setText("00:00");
            }
            return false;
        }
    });


    KeyBoardBarViewListener mKeyBoardBarViewListener;

    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) {
        this.mKeyBoardBarViewListener = l;
    }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public interface KeyBoardBarViewListener {

        void OnKeyBoardStateChange(int state, int height);

        /**
         * 点击发送事件反馈
         *
         * @param msg 输入的内容
         */
        void onKeyBoardSendMsg(String msg);

        void onKeyBoardPhoto();

        void onKeyBoardCamera();

        /**
         * 录音文件
         *
         * @param voiceFile 录音文件的路径
         */
        void onKeyBoardVoice(String voiceFile, int time);

    }
}

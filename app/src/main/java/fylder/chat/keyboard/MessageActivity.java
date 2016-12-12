package fylder.chat.keyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import fylder.keyboard.lib.KeyBoardBar;

/**
 * Created by 剑指锁妖塔 on 2016/06/11.
 */
public class MessageActivity extends AppCompatActivity implements KeyBoardBar.KeyBoardBarViewListener {

    protected KeyBoardBar chatLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
    }

    void init() {
        chatLay = (KeyBoardBar) findViewById(R.id.chat_lay);
        chatLay.setOnKeyBoardBarViewListener(this); //注册监听事件
    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {

    }

    @Override
    public void onKeyBoardSendMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyBoardPhoto() {
        Toast.makeText(this, "点击相册", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyBoardCamera() {
        Toast.makeText(this, "点击相机", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyBoardVoice(String voiceFile, int time) {
        Toast.makeText(this, "录音了" + time + "s", Toast.LENGTH_SHORT).show();
    }
}

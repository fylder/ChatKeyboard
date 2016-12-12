package fylder.chat.keyboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import fylder.keyboard.lib.utils.EmoticonsUtils;

/**
 * Created by 剑指锁妖塔 on 2016/06/11.
 */
public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        EmoticonsUtils.initEmoticonsDB(this);//初始表情数据

        findViewById(R.id.demo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
    }
}

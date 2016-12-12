package fylder.keyboard.lib.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 录音工作
 * Created by fylder on 2015/9/10.
 */
public class VoiceUtils {

    private MediaRecorder mMediaRecorder;

    private static VoiceUtils voiceUtils;

    private String voiceFilePath;
    private String audioPath;

    public static VoiceUtils getInstance() {
        if (voiceUtils == null) {
            voiceUtils = new VoiceUtils();
        }
        return voiceUtils;
    }

    public VoiceUtils() {
        initPath();
    }

    public MediaRecorder getmMediaRecorder() {
        return mMediaRecorder;
    }

    private void initPath() {
        voiceFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fylder/temp";
        File file = new File(voiceFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            }

            audioPath = getVoicePath();

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //检测权限是否开启 尚未解决 --issue 2015-09-10 19:26:29
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(audioPath);
            mMediaRecorder.prepare();
            mMediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束录音
     */
    public String stopRecord() {

        if (mMediaRecorder != null) {
            try {
                // mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                return audioPath;
            } catch (Exception e) {
                Log.e("123", e.toString());
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 创建录音文件名
     *
     * @return 文件路径
     */
    private String getVoicePath() {
        return voiceFilePath + "/audio" + new Date().getTime() + ".amr";
    }
}

package fylder.keyboard.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fylder on 2015/9/10.
 */
public class TimeTools {

    /**
     * 计算时间
     *
     * @param startTime 开始时间
     * @return 00:00
     */
    public static String getRecordTime(long startTime) {
        long nowTime = new Date().getTime();
        long t = nowTime - startTime;
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(t));
    }
}

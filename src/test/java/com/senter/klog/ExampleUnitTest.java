package com.senter.klog;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDelDate() {
        final int KEEP_DAYS = 30;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = sf.format(new Date());
        Date keepDay = new Date();
        try {
            keepDay = sf.parse(today.split(" ")[0] + " " + "00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log("keepday:" + keepDay + ", keepDay.ms:" + keepDay.getTime());
        keepDay = new Date(keepDay.getTime() - (KEEP_DAYS - 1) * 24 * 3600 * 1000);
        log(">> " + (keepDay.getTime() - KEEP_DAYS * 24 * 3600 * 1000));
        log(">> " + (keepDay.getTime() - KEEP_DAYS * 24 * 3600 * 1000L));
        log(">> " + KEEP_DAYS * 24 * 3600 * 1000);
        log(">> " + KEEP_DAYS * 24 * 3600 * 1000L);


        log("删除截止时间：" + keepDay);
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
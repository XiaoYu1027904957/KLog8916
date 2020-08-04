package com.senter.klog;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void test() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.senter.klog.test", appContext.getPackageName());
        LogCtrl.init("860723043318607", "stmonitag");
        KLog.d("========================================begin");
        KLog.k("kkkkkk");
        KLog.k("tag","kkkkkk");
        KLog.e("error");
        KLog.e(new Exception());
        KLog.e("tag1",new Exception("exception msg 1"), new Exception("exception msg 2"));
        KLog.w("waining");
        KLog.p("myekey","myvalue","label");
        KLog.i("info");
        HashMap<String,String[]> map = new HashMap<>();
        map.put("key1",new String[]{"value1","label"});
        map.put("vlueempty",new String[]{"value1","label"});
        map.put("key2",new String[]{"value2","label"});
        map.put("",new String[]{"value1","label"});
        map.put("key3",new String[]{"value13","label3"});
        map.put(null, new String[]{"value1r","labelr"});
        map.put("key5", new String[]{"value1","label"});
        KLog.p(map);
        KLog.d("debug");
        KLog.d("subtag","debug");
        KLog.v("verbose");
        KLog.json("{channelWatermark : [{\"meid\":\"A10000\",\"watermark\":\"水印内容\"},{\"meid\":\"A10000\",\"watermark\":\"水印内容\"},{\"meid\":\"A10000\",\"watermark\":\"水印内容\"}]}");
        KLog.xml("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<ScrollView  xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\">\n" +
                "    <LinearLayout\n" +
                "        android:id=\"@+id/test_main\"\n" +
                "        android:orientation=\"vertical\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\">\n" +
                "    </LinearLayout>\n" +
                "</ScrollView>");
        KLog.d("1");
        KLog.d("2");
        KLog.trace();
        KLog.d("3");
        KLog.d("4");
        KLog.debug();
        KLog.d("5");
//        testMax();
//        testFailed();
        KLog.d("*********************************************end");
        int i =0;
        while (i< 10){
            SystemClock.sleep(1000);
            i++;
        }
    }
    @Test
    public void testDebug() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.senter.klog.test", appContext.getPackageName());
//        SysProp.set("persist.sys.stklog_debug","1");
        assertEquals(LogCtrl.isDebugMode(), true);
        LogCtrl.init("860723043318607", "stmonitag");
        KLog.d("========================================begin");
        KLog.e("error");
        KLog.w("waining");
        KLog.p("myekey","myvalue","");
        KLog.i("info");
        HashMap<String,String[]> map = new HashMap<>();
        map.put("key1",new String[]{"value1","label"});
        map.put("vlueempty",new String[]{"value1","label"});
        map.put("key2",new String[]{"value2","label"});
        map.put("",new String[]{"value1","label"});
        map.put("key3",new String[]{"value13","label3"});
        map.put(null, new String[]{"value1r","labelr"});
        map.put("key5", new String[]{"value1","label"});
        KLog.p(map);
        KLog.d("debug");
        KLog.d("subtag","debug");
        KLog.v("verbose");
        KLog.json("{channelWatermark : [{\"meid\":\"A10000\",\"watermark\":\"水印内容\"},{\"meid\":\"A10000\",\"watermark\":\"水印内容\"},{\"meid\":\"A10000\",\"watermark\":\"水印内容\"}]}");
        KLog.xml("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<ScrollView  xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\">\n" +
                "    <LinearLayout\n" +
                "        android:id=\"@+id/test_main\"\n" +
                "        android:orientation=\"vertical\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\">\n" +
                "    </LinearLayout>\n" +
                "</ScrollView>");
        KLog.d("*********************************************end");
        int i =0;
        while (i< 10){
            SystemClock.sleep(1000);
            i++;
        }
    }


    void testFailed() {
        new Thread() {
            @Override
            public void run() {
                long ii = 0;
                while (ii < Long.MAX_VALUE) {
                    KLog.d("c" + ii++);
                    SystemClock.sleep(100);
                }
            }
        }.start();
    }

    void testMax() {
        new Thread() {
            @Override
            public void run() {
                int M = 1000;
                int BASE = 85;
                long i = 0;
                String str = "";
                while (str.length() < M * 2) {
                    str = str + str + i;
                }
                str = str.substring(0, M);
                KLog.d("a");
                KLog.d("a");
                KLog.d("a");
                KLog.d("a");
                String str1 = str.substring(0, str.length() - BASE + 1);
                KLog.d(str1);
                KLog.d("b");
                KLog.d("b");
                KLog.d("d" + (str1.length() + BASE));
                KLog.d("b");
                KLog.d("b");
                str1 = str.substring(0, str.length() - BASE);
                KLog.d(str1);
                KLog.d("c");
                KLog.d("c");
                KLog.d("c" + (str1.length() + BASE));
                KLog.d("c");
                KLog.d("c");
                str1 = str.substring(0, str.length() - (BASE + 1) * 3);
                KLog.d(str1);
                KLog.d("d");
                KLog.d("d");
                KLog.d("d" + (str1.length() + BASE));
                KLog.d("d");
                KLog.d("d");
            }
        }.start();
    }


    static void aa() {
        try {
            Integer.parseInt("teset exception");
        } catch (Exception e) {
            KLog.e("tgb", e, e);
        }

        try {
            Integer.parseInt("teset exception2");
        } catch (Exception e) {
            KLog.e("tgb", e);
        }
    }
}

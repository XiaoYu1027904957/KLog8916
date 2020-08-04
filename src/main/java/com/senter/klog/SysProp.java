package com.senter.klog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tgb on 18-1-19.
 */

public class SysProp {
    private static Method sysPropGet;
    private static Method sysPropSet;
    private static Method getBoolean;
    private static Method getLong;

    private SysProp() {
    }

    static {
        try {
            Class<?> S = Class.forName("android.os.SystemProperties");
            Method M[] = S.getMethods();
            for (Method m : M) {
                String n = m.getName();
                if (n.equals("get")) {
                    sysPropGet = m;
                } else if (n.equals("set")) {
                    sysPropSet = m;
                } else if (n.equals("getBoolean")) {
                    getBoolean = m;
                } else if (n.equals("getLong")) {
                    getLong = m;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String get(String name, String default_value) {
        try {
            return (String) sysPropGet.invoke(null, name, default_value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return default_value;
    }

    public static boolean set(String name, String value) {
        try {
            sysPropSet.invoke(null, name, value);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getBoolean(String key, boolean def) {
        try {
            return (Boolean) getBoolean.invoke(null, key, def);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return def;
    }

    public static long getLong(String key, long def) {
        try {
            return (long) getLong.invoke(null, key, def);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return def;
    }
}

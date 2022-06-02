package com.payu.baas.coreUI.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
Converting JSON data to Gson data (models)
 */
public class JsonUtil {
    public static String toString(Object o) {
        return getGson().toJson(o);
    } // object to string
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object toObject(String json, Class type) { // json string to object conversion
        return getGson().fromJson(json, type);
    }
    public static Gson getGson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}

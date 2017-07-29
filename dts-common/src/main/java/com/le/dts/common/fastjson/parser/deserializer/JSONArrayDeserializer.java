package com.le.dts.common.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.le.dts.common.fastjson.JSONArray;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.JSONToken;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;

public class JSONArrayDeserializer implements ObjectDeserializer {
    public final static JSONArrayDeserializer instance = new JSONArrayDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONArray array = new JSONArray();
        parser.parseArray(array);
        return (T) array;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}

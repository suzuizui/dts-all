package com.le.dts.common.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.le.dts.common.fastjson.JSONException;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.JSONToken;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;

public class DateFormatDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {

    public final static DateFormatDeserializer instance = new DateFormatDeserializer();

    @SuppressWarnings("unchecked")
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
        
        if (val == null) {
            return null;
        }

        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            
            return (T) new SimpleDateFormat(strVal);
        }

        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}

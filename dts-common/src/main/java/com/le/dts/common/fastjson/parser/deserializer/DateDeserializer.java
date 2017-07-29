package com.le.dts.common.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;

import com.le.dts.common.fastjson.JSONException;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.JSONScanner;
import com.le.dts.common.fastjson.parser.JSONToken;
import com.le.dts.common.fastjson.JSONException;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.JSONScanner;

public class DateDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {

    public final static DateDeserializer instance = new DateDeserializer();

    @SuppressWarnings("unchecked")
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {

        if (val == null) {
            return null;
        }

        if (val instanceof java.util.Date) {
            return (T) val;
        } else if (val instanceof Number) {
            return (T) new java.util.Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }

            JSONScanner dateLexer = new JSONScanner(strVal);
            try {
                if (dateLexer.scanISO8601DateIfMatch(false)) {
                    return (T) dateLexer.getCalendar().getTime();
                }
            } finally {
                dateLexer.close();
            }

            DateFormat dateFormat = parser.getDateFormat();
            try {
                return (T) dateFormat.parse(strVal);
            } catch (ParseException e) {
                // skip
            }

            long longVal = Long.parseLong(strVal);
            return (T) new java.util.Date(longVal);
        }

        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}

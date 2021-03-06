package com.le.dts.common.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}

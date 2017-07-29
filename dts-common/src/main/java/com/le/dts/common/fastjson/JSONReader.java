package com.le.dts.common.fastjson;

import static com.le.dts.common.fastjson.JSONStreamContext.ArrayValue;
import static com.le.dts.common.fastjson.JSONStreamContext.PropertyKey;
import static com.le.dts.common.fastjson.JSONStreamContext.PropertyValue;
import static com.le.dts.common.fastjson.JSONStreamContext.StartArray;
import static com.le.dts.common.fastjson.JSONStreamContext.StartObject;

import java.io.Closeable;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.Feature;
import com.le.dts.common.fastjson.parser.JSONLexer;
import com.le.dts.common.fastjson.parser.JSONReaderScanner;
import com.le.dts.common.fastjson.parser.JSONToken;
import com.le.dts.common.fastjson.util.IOUtils;
import com.le.dts.common.fastjson.util.TypeUtils;
import com.le.dts.common.fastjson.parser.DefaultJSONParser;
import com.le.dts.common.fastjson.parser.Feature;
import com.le.dts.common.fastjson.parser.JSONLexer;
import com.le.dts.common.fastjson.parser.JSONReaderScanner;
import com.le.dts.common.fastjson.util.IOUtils;
import com.le.dts.common.fastjson.util.TypeUtils;

public class JSONReader implements Closeable {

    private final DefaultJSONParser parser;
    private JSONStreamContext       context;

    public JSONReader(Reader reader){
        this(new JSONReaderScanner(reader));
    }

    public JSONReader(JSONLexer lexer){
        this(new DefaultJSONParser(lexer));
    }

    public JSONReader(DefaultJSONParser parser){
        this.parser = parser;
    }

    public void config(Feature feature, boolean state) {
        this.parser.config(feature, state);
    }

    public void startObject() {
        if (context == null) {
            context = new JSONStreamContext(null, JSONStreamContext.StartObject);
        } else {
            startStructure();
            context = new JSONStreamContext(context, JSONStreamContext.StartObject);
        }

        this.parser.accept(JSONToken.LBRACE, JSONToken.IDENTIFIER);
    }

    public void endObject() {
        this.parser.accept(JSONToken.RBRACE);
        endStructure();
    }

    public void startArray() {
        if (context == null) {
            context = new JSONStreamContext(null, JSONStreamContext.StartArray);
        } else {
            startStructure();

            context = new JSONStreamContext(context, JSONStreamContext.StartArray);
        }
        this.parser.accept(JSONToken.LBRACKET);
    }

    public void endArray() {
        this.parser.accept(JSONToken.RBRACKET);
        endStructure();
    }

    private void startStructure() {
        final int state = context.getState();
        switch (state) {
            case JSONStreamContext.PropertyKey:
                parser.accept(JSONToken.COLON);
                break;
            case JSONStreamContext.PropertyValue:
            case JSONStreamContext.ArrayValue:
                parser.accept(JSONToken.COMMA);
                break;
            case JSONStreamContext.StartArray:
            case JSONStreamContext.StartObject:
                break;
            default:
                throw new JSONException("illegal state : " + context.getState());
        }
    }

    private void endStructure() {
        context = context.getParent();

        if (context == null) {
            return;
        }
        
        final int state = context.getState();
        int newState = -1;
        switch (state) {
            case JSONStreamContext.PropertyKey:
                newState = JSONStreamContext.PropertyValue;
                break;
            case JSONStreamContext.StartArray:
                newState = JSONStreamContext.ArrayValue;
                break;
            case JSONStreamContext.PropertyValue:
            case JSONStreamContext.StartObject:
                newState = JSONStreamContext.PropertyKey;
                break;
            default:
                break;
        }
        if (newState != -1) {
            context.setState(newState);
        }
    }

    public boolean hasNext() {
        if (context == null) {
            throw new JSONException("context is null");
        }

        final int token = parser.getLexer().token();
        final int state = context.getState();
        switch (state) {
            case JSONStreamContext.StartArray:
            case JSONStreamContext.ArrayValue:
                return token != JSONToken.RBRACKET;
            case JSONStreamContext.StartObject:
            case JSONStreamContext.PropertyValue:
                return token != JSONToken.RBRACE;
            default:
                throw new JSONException("illegal state : " + state);
        }
    }

    public void close() {
        IOUtils.close(parser);
    }

    public Integer readInteger() {
        Object object;
        if (context == null) {
            object = parser.parse();
        } else {
            readBefore();
            object = parser.parse();
            readAfter();
        }

        return TypeUtils.castToInt(object);
    }

    public Long readLong() {
        Object object;
        if (context == null) {
            object = parser.parse();
        } else {
            readBefore();
            object = parser.parse();
            readAfter();
        }

        return TypeUtils.castToLong(object);
    }

    public String readString() {
        Object object;
        if (context == null) {
            object = parser.parse();
        } else {
            readBefore();
            object = parser.parse();
            readAfter();
        }

        return TypeUtils.castToString(object);
    }
    
    public <T> T readObject(TypeReference<T> typeRef) {
        return readObject(typeRef.getType());
    }

    public <T> T readObject(Type type) {
        if (context == null) {
            return parser.parseObject(type);
        }

        readBefore();
        T object = parser.parseObject(type);
        readAfter();
        return object;
    }

    public <T> T readObject(Class<T> type) {
        if (context == null) {
            return parser.parseObject(type);
        }

        readBefore();
        T object = parser.parseObject(type);
        readAfter();
        return object;
    }

    public void readObject(Object object) {
        if (context == null) {
            parser.parseObject(object);
            return;
        }

        readBefore();
        parser.parseObject(object);
        readAfter();
    }

    public Object readObject() {
        if (context == null) {
            return parser.parse();
        }

        readBefore();
        Object object;
        switch (context.getState()) {
            case JSONStreamContext.StartObject:
            case JSONStreamContext.PropertyValue:
                object = parser.parseKey();
                break;
            default:
                object = parser.parse();
                break;
        }

        readAfter();
        return object;
    }

    @SuppressWarnings("rawtypes")
    public Object readObject(Map object) {
        if (context == null) {
            return parser.parseObject(object);
        }

        readBefore();
        Object value = parser.parseObject(object);
        readAfter();
        return value;
    }

    private void readBefore() {
        int state = context.getState();
        // before
        switch (state) {
            case JSONStreamContext.PropertyKey:
                parser.accept(JSONToken.COLON);
                break;
            case JSONStreamContext.PropertyValue:
                parser.accept(JSONToken.COMMA, JSONToken.IDENTIFIER);
                break;
            case JSONStreamContext.ArrayValue:
                parser.accept(JSONToken.COMMA);
                break;
            case JSONStreamContext.StartObject:
                break;
            case JSONStreamContext.StartArray:
                break;
            default:
                throw new JSONException("illegal state : " + state);
        }
    }

    private void readAfter() {
        int state = context.getState();
        int newStat = -1;
        switch (state) {
            case JSONStreamContext.StartObject:
                newStat = JSONStreamContext.PropertyKey;
                break;
            case JSONStreamContext.PropertyKey:
                newStat = JSONStreamContext.PropertyValue;
                break;
            case JSONStreamContext.PropertyValue:
                newStat = JSONStreamContext.PropertyKey;
                break;
            case JSONStreamContext.ArrayValue:
                break;
            case JSONStreamContext.StartArray:
                newStat = JSONStreamContext.ArrayValue;
                break;
            default:
                throw new JSONException("illegal state : " + state);
        }
        if (newStat != -1) {
            context.setState(newStat);
        }
    }

}

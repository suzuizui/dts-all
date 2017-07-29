package com.le.dts.console.undo;

import java.util.List;

/**
 * Undo数据库;
 * Created by luliang on 15/1/14.
 */
public class UndoContent {

    private Object content;

    private List<Object> relationList;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public List<Object> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<Object> relationList) {
        this.relationList = relationList;
    }
}

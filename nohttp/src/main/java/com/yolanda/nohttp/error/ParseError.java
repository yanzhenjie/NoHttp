/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © ZhiMore. All Rights Reserved
 *
 */
package com.yolanda.nohttp.error;

/**
 * Created on 2016/6/30.
 *
 * @author Yan Zhenjie: QQ: 757699476.
 */
public class ParseError extends Exception {

    public ParseError() {
    }

    public ParseError(String detailMessage) {
        super(detailMessage);
    }

    public ParseError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseError(Throwable throwable) {
        super(throwable);
    }
    
}

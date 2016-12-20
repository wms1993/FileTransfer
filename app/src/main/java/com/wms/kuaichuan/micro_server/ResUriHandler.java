package com.wms.kuaichuan.micro_server;

/**
 * Created by 王梦思 on 2016/12/14.
 * <p/>
 */
public interface ResUriHandler {

    /**
     * is matches the specify uri
     */
    boolean matches(String uri);


    /**
     * handler the request which matches the uri
     */
    void handler(Request request);

    /**
     * releas some resource when finish the handler
     */
    void destroy();
}

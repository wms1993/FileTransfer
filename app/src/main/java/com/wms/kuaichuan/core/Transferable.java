package com.wms.kuaichuan.core;

/**
 * Created by 王梦思 on 2016/11/10.
 * <p/>
 */
public interface Transferable {


    /**
     *
     * @throws Exception
     */
    void init() throws Exception;


    /**
     *
     * @throws Exception
     */
    void parseHeader() throws Exception;


    /**
     *
     * @throws Exception
     */
    void parseBody() throws Exception;


    /**
     *
     * @throws Exception
     */
    void finish() throws Exception;
}

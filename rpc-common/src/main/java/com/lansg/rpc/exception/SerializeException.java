package com.lansg.rpc.exception;

/**
* @author: Lansg
* @date: 2022/11/23 20:29
* @Description: 序列化异常
*/
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}

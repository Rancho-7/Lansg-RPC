package com.lansg.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @author: Lansg
* @date: 2022/11/22 16:26
* @Description: 字节流中标识序列化和反序列化器
*/
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1);
    private final int code;
}

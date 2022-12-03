package com.lansg.rpc.serializer;


/**
* @author: Lansg
* @date: 2022/11/22 20:35
* @Description: 通用的序列化反序列化接口
*/
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    //code表示使用哪种学序列化接口
    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
}

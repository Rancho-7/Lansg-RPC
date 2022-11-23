package com.lansg.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lansg.rpc.api.HelloObject;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HelloObject o =new HelloObject(1,"hi");

        byte[] json = objectMapper.writeValueAsBytes(o);
        for (byte b:json){
            System.out.println(b);
        }
//        System.out.println(json);

    }
}

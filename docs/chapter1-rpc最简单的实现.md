### 实现思路

首先我们需要搞清楚RPC的大致原理是怎样的。provider暴露出服务的接口，consumer发现并调用该接口，并通过网络将接口的一些相关信息（调用的接口名，参数列表和参数类型等）传输给provider，provider找到该接口的具体实现类，执行相关方法后将结果作为返回值返回给consumer。

接下来将从以下几个方面入手，实现一个简单的RPC。



#### 服务接口

由于方法的重载，provider可能会存在许多重名的方法，为了准确调用到我们需要的方法，provider端需要将方法的名称和参数等信息都发送给provider。

服务接口我们称为`HelloService`：

```java
public interface HelloService {
    String hello(HelloObject object);
}
```

hello方法需要传递一个对象，HelloObject对象，定义如下：

```java
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
```

注意这个对象需要实现`Serializable`接口，因为它需要在调用过程中从客户端传递给服务端。

接着我们在服务端对这个接口进行实现，实现的方式也很简单，返回一个字符串就行：

```java
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject obj) {
        log.info("接收到消息:{}",obj.getMessage());
        return "这是调用的返回值,id="+obj.getId();
    }
}
```

#### 传输协议

consumer在调用服务接口并向provider传递信息时，双方应当规定一种传递信息的方式，不然provider就无法对信息进行识别了。我们可以将接口名称，参数列表等信息写到一个对象里，传输时只需传递此对象就可以了。

```java
@Data
@Builder
public class RpcRequestBean implements Serializable {
    //待调用接口名称
    private String interfaceName;

    //待调用方法名称
    private String methodName;

    //调用方法参数列表
    private Object[] parameters;

    //调用方法的参数类型
    private Class<?>[] paramTypes;
}
```

同理，provider给consumer返回信息时，也可以封装为一个对象。

```java
@Data
public class RpcResponseBean<T> implements Serializable {
    //响应状态码
    private Integer code;

    //响应数据
    private T data;

    //补充信息
    private String message;

    public static <T> RpcResponseBean<T> success(T data){
        RpcResponseBean<T> response=new RpcResponseBean<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    public static <T> RpcResponseBean<T> fail(ResponseCode code){
        RpcResponseBean<T> response=new RpcResponseBean<>();
        response.setCode(code.getCode());
        response.setMessage(code.getMessage());
        return  response;
    }
}
```

#### consumer实现

#### provider实现
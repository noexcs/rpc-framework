# RPC-framework

#### 介绍

一个简易的，轻量级的，Java远程调用框架。

#### 软件架构
软件架构说明


#### 安装教程

1. rpc服务器端引入以下maven坐标
    ```xml
       <dependencies>
            <dependency>
                <groupId>org.noexcs</groupId>
                <artifactId>rpc-provider-server</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
    ```
   添加服务端配置文件：`rpc-provider-config.yaml`
    ```yaml
    # 本地服务地址端口
    server:
      host: "localhost"
      port: 8007
    
   # 注册中心地址，如果开启的话就会向注册中心注册服务
    registry:
      enabled: false
      server: 127.0.0.1
      port: 8848
      # 提供服务名称
      serviceName: rpc-public
    ```
2. rpc客户端引入以下maven坐标
   ```xml
       <dependencies>
            <dependency>
                <groupId>org.noexcs</groupId>
                <artifactId>rpc-consumer-client</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
   ```
   添加客户端配置文件：`rpc-consumer-config.yml`
    ```yaml
    # 直接连接服务端，服务端地址
    provider:
      server: 127.0.0.1
      port: 8007
   
   # 注册中心地址，如果开启的话就会向注册中心发现服务
    registry:
      enabled: false
      type: nacos # 注册中心类型 暂时只支持 nacos
      serviceName: rpc-public  # 服务名称
      server: 127.0.0.1
      port: 8848
      # 负载均衡策略  需继承org.noexcs.loadBalance.AbstractLoadBalance
      loadBalancer: org.noexcs.loadBalance.impl.RandomBalance

    ```

#### 使用说明

1. 服务端

   以类的方式定义rpc服务，并使用`@Service`注解标注为Rpc服务:
    ```java
   import org.springframework.stereotype.Service;
   
    @Service
    public class StringUppercaseService {
   
        String upperCaseString(String s){
            return s.toUpperCase();
        }
   
    }
    ```
   启动服务：
   ```java
   public class ServerMain {
       public static void main(String[] args) {
           RpcServer.start(ServerMain.class);
       }
   }
   ```
2. 客户端

   以服务端定义的服务编写对应的接口：
    ```java
    public interface StringUppercaseService {
    
        String upperCaseString(String s);
   
    }
    ```
   > 接口的全限定名需要与服务端保持一致，所调用的方法也要与服务端保持一致（包括方法名，形参列表）
   > 

   调用服务：
   ```java
   public class ConsumerMain {
       public static void main(String[] args) {
           StringUpperCaseService upperCaseService = new RpcClientProxy().getProxy(StringUpperCaseService.class);
           String s = upperCaseService.upperCaseString("Hello World!");
           System.out.println(s);
       }
   }
   ```


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

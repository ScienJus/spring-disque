# Spring Disque

基于 Spring 和 Jedis 的 Disque 封装，使用注解驱动

###关于 Disque

> Disque 是一个内存储存的分布式任务队列实现， 它由 Redis 的作者 Salvatore Sanfilippo (@antirez)开发， 目前正处于预览版（alpha）阶段。

一些介绍：

 - 该项目的地址：[Disque, an in-memory, distributed job queue][1]
 - 该项目的中文介绍：[Disque 使用教程][2]
 - Java 的客户端实现（Jedis 的作者开发）：[Jedisque][3]

[1]: https://github.com/antirez/disque
[2]: http://disquebook.com/
[3]: https://github.com/xetorthio/jedisque

### 使用方法

**创建项目**

仓库：

```
<repository>
    <id>scienjus-mvn-repo</id>
    <url>https://raw.github.com/ScienJus/maven/snapshot/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

依赖：

```
<dependency>
    <groupId>com.scienjus</groupId>
    <artifactId>spring-disque</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

所有依赖 Jar：

```
<properties>
  <spring.version>4.1.8.RELEASE</spring.version>
  <jedisque.version>0.0.4</jedis.version>
  <aspectj.version>1.8.7</aspectj.version>
  <quartz.version>2.2.1</quartz.version>
</properties>

<dependencies>
  <dependency>
    <groupId>com.github.xetorthio</groupId>
    <artifactId>jedisque</artifactId>
    <version>${jedisque.version}</version>
  </dependency>

  <!-- For quartz -->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>${quartz.version}</version>
  </dependency>

  <!--For aop-->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>${aspectj.version}</version>
  </dependency>
</dependencies>
```

**配置 Spring Bean**

配置 Jedisque 客户端：

```
@Bean
public Jedisque jedisque() {
    try {
        Jedisque jedisque = new Jedisque(new URI("disque://192.168.1.222:7711"));
        return jedisque;
    } catch (URISyntaxException e) {
        return null;
    }
}
```

配置消费者：

```
@Bean
public DisqueConsumer consumer() {
    DisqueConsumer consumer = new DisqueConsumer();
    consumer.setJedisque(jedisque());
    return consumer;
}
```

配置生产者：

```
@Bean
public DisqueProducer producer() {
    DisqueProducer producer = new DisqueProducer();
    producer.setJedisque(jedisque());
    return producer;
}
```

配置消费者定时扫描任务（仅当使用注解驱动的消费者时才需要配置）：

```
@Bean(initMethod = "init", destroyMethod = "destroy")
public SchedulerBeanFactory schedulerBeanFactory() {
    SchedulerBeanFactory schedulerBeanFactory = new SchedulerBeanFactory();
    schedulerBeanFactory.setConsumer(consumer());
    return schedulerBeanFactory;
}
```

注意一定要将`initMethod`设为`init`方法。

配置生产者自动推送任务（仅当使用注解驱动的生产者时才需要配置）：

```
@Bean
public ProducerWorker producerWorker() {
    ProducerWorker producerWorker = new ProducerWorker();
    producerWorker.setProducer(producer());
    return producerWorker;
}
```

**创建生产者实例**

在类上使用`@Producer`注解，在方法上使用`@AddJob`注解，`retrun`需要发送的对象（需要配置`producerWorker`）：

```
@Producer
public class SayHelloProducer {

    @AddJob(queue = "say_hello")
    public String sayHello(String name) {
        return name;
    }
}
```

**创建消费者实例**

在类上使用`@Consumer`注解，在方法上使用`@GetJob`注解（需要配置`schedulerBeanFactory`）：

```
@Consumer
public class SayHelloConsumer {

    @GetJob(queue = "say_hello")
    public boolean onSayHello(String name) {
        System.out.println("Hello ! " + name + " !");
        return true;
    }
}
```

**消费者的重试机制**

当`@GetJob`方法的返回值类型为`boolean`类型，并且执行的结果为`false`时，系统认定此任务执行失败。

当方法的返回值为`Void`或是执行的结果为`true`时，系统认定任务执行成功。

执行失败的任务将会在一段时间后重新投递，直到执行成功或超过任务的生存周期。

### 待办事项

- [ ] 任务参数
- [ ] 监控页面

### 联系方式

可以提 Issues 或是通过邮件联系我：`i@scienjus.com`

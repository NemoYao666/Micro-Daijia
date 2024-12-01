# Micro-Daijia
# 基于微信小程序的微服务代驾系统后台

基于微信小程序，采用JDK17、SpringBoot、MySQL、MyBatisPlus、SpringCloud微服务开发  
基于CentOS7 Docker部署Redis、RabbitMQ、MongoDB、Minio  
支持Swagger API文档，Nacos服务注册配置，微信授权登录，腾讯云COS对象存储、OCR与人脸识别司机认证，腾讯云位置服务路线规划，Drools规则引擎金额计算，XXL定时任务调度乘客下单，Redis分布式锁司机抢单，MongoDB订单执行GPS坐标存储，SEATA分布式事务订单支付，RabbitMQ订单支付完成保证最终一致性
****

## Running Environment
服务列表 与 WEB UI
```
# win
mysql
nacos          http://127.0.0.1:8848
seata          http://127.0.0.1:7091
xxl            http://127.0.0.1:8080/xxl-job-admin/
knife4j        http://127.0.0.1:<port>/doc.html
swagger        http://127.0.0.1:<port>/swagger-ui/index.html

# linux docker IP:104
redis          
rabbitMQ       http://192.168.88.104:15672
minio          http://192.168.88.104:9000
mongodb        
```
启动流程
```
# win
mysql启动  
nacos注册中心非集群启动: `.\startup.cmd -m standalone`  
seata分布式事务框架启动
xxl任务调度中心: xxl-job-master   
微信小程序前端：mp-weixin-drver, mp-weixin-customer
```
```shell
# docker linux IP:104
systemctl start docker # redis rabbitMQ mongodb minio auto start
# redis cmd
docker exec -it gmalldocker_redis redis-cli
# mongodb cmd
docker exec -it mongo mongosh
```
    
****
# Project Architecture
## daijia-parent 项目架构
```
daijia-parent/
├── common                          // 工具组件配置
│   ├── common-log                  // 日志配置
│   ├── common-util                 // 统一响应数据结构，工具类
│   ├── rabbit-util                 // rabbitMQ工具类
│   └── service-util                // knife4j，MyBatisPlus，Redis，全局异常处理，常量定义
├── model                           // entity，enums，form，query，vo
├── server-gateway                  // 服务端网关
├── server                          // 服务端
│   ├── service-coupon              // 优惠卷
│   ├── service-customer            // 乘客
│   ├── service-dispatch            // 任务调度
│   ├── service-driver              // 司机
│   ├── service-map                 // 地图
│   ├── service-order               // 订单
│   ├── service-payment             // 支付
│   └── service-rules               // 规则
├── service-client                  // 服务端供外部调用的Feign-Client
│   ├── service-coupon-client       // 优惠卷
│   ├── service-customer-client     // 乘客
│   ├── service-dispatch-client     // 任务调度
│   ├── service-driver-client       // 司机
│   ├── service-map-client          // 地图
│   ├── service-order-client        // 订单
│   ├── service-payment-client      // 支付
│   └── service-rules-client        // 规则
└── web                             // 业务接口
    ├── web-custormer               // 乘客端接口
    └── web-driver                  // 司机端接口
```










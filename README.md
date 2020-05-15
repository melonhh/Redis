# Redis
#### 应用架构（数据的存储与处理方面）的演变
1. 单机MySQL的阶段
2. Memcached/redis/mongoDB（缓存）+ MySQL + 垂直分离阶段
3. MySQL主从复制、读写分离阶段
4. 分库分表+MySQL集群阶段
5. 今天的框架
    * 负载均衡
    * 动静分离
    * 分布式文件系统
    * 缓存 + 主从复制读写分离 + 垂直拆分、水平拆分

### Redis入门
> 简介  
1. 是什么？
    * Remote Dictionary Server 远程字典服务器
    * 一个高性能的（key/value）分布式内存数据库
    * 基于内存运行并支持持久化的NoSQL数据库
2. 能干嘛？
    * 内存存储和持久化：redis支持异步将内存中的数据写到硬盘，同时不影响继续服务
    * 缓存
    * 过期功能
    * 发布、订阅消息系统
    * 定时器、计数器
3. 官网
    * www.redis.cn

> 安装  
1. 安装上传下载工具包rz、sz --- `yum install -y lrzsz`
2. 使用SecureCRT rz上传压缩文件
3. 解压：tar -zxvf redis-5.0.5.tar.gz
4. 进入 cd redis-5.0.5
5. make命令编译并安装 ---- `make PREFIX=/usr/local/redis install`
6. 把安装包里的redis.config 文件复制到安装目录

> bin目录中的可执行文件（暂时使用两个）  
1. redis-server  服务器
2. redis-cli   客户端

> Redis的启动和关闭 (默认端口号6379)  
1. 直接启动（不建议）
    * 直接执行redis-server
    * 这种方式启动redis不能进行集群
2. 指定配置文件启动，可以进行集群
    * 修改配置文件redis.conf的136行守护进程改为： daemonize yes
    * 启动redis-server 后 跟上配置文件路径
    * 这种方式以后台方式启动
3. 关闭Redis
    * 第一种：杀死进程  
        kill -9 pid
    * 第二种：调用客户端关闭  
        运行redis-cli shutdown

> 使用Redis客户端连接Redis服务器  
1. 本机  
    直接运行本机redis-cli（使用默认6379端口）
2. 指定ip地址和端口连接redis  
    redis-cli -h ip地址 -p 端口号  
3. 针对2，有两个注意点
    1. 可以注释掉配置文件里的bind  
        这样可以连接，但不可读写数据，需设置protected-mode no
    2. 不注释掉bind，将连接的主机加入bind

### Redis数据类型
> 数据形式 ：key-value  
1. key
2. value
    * 字符串
    * 列表list
    * 散列hash
    * 无序集合set
    * 有序集合set
    * 基数统计HyperLogLog
    * 地理地图GEO
    * 流Stream （5.0新增）
> 1 字符串  
1. Redis的字符串是二进制安全的（为什么呢）  
    因为Redis只有客户端存在编码和解码，服务器和磁盘存储的是二进制
2. Redis内部使用三种编码类型来保存字符串对象的数据
    1. int：用来保存64位有符号整数形式的字符串
    2. embstr：用来保存小于或等44字节大小的字符串
    3. raw：用来保存大于44字节的字符串  
    可以使用object encoding key查看这种内部编码
3. set k1 value 双引号科协可不写  
    变种：setnx key1 只有key1不存在时才把后面的内容放到key1中
4. del命令
5. strlen k1 --- 返回对应值的字符个数（不存在返回0）
6. append k1 追加内容
7. setrange k1 3 从第三个字符开始覆盖的内容
8. getset k1 --- 先取后覆盖
9. incr numkey --- 自增1  
    变种：incrby numkey 设置的步长
10. decr numkey --- 自减1
    变种：decrby numkey 设置步长

> 2 列表list类型  
> （这个列表底层不是用数组来实现的，类似双向链表结构）  
1. 在链表左端插入a、b、c、d  
    lpush list1 a b c d
2. 在链表右端插入
    rpush list2 a b c d
3. 获取列表中的所有数据： 0-start、1-end  
    lrange list1 0 -1
4. 在指定的数据项前/后追加新数据（效率低）  
    linsert list1 after/before b f (在第一个b的后/前插入f)  
5. 获取列表指定索引上的值（效率低）  
    lindex list1 2
6. 删除列表中的数据项
    lpop list1  
    rpop list1  
    （redis中的列表没有空列表之说，数据项都删除后，列表也就被删除了）
7. 获取列表长度  
    llen list1
8. 删除指定的数据项（可以指定删除的数量和方向）  
    lrem list1 count value  
    （count ： 0---删除所有与value相等的项，>0---从头开始搜索（删除个数位count），<0---从表尾开始）
9. 通过索引替换值（效率低）  
    lset list1 1 f
    
> 3 哈希hash类型（把hash中的key称为字段field）  
1. hset hash1 field1 value1 ---- 一次只能设置一个键值对
2. hget hash1 field1 ---- 获取指定field的值
3. hmset hash2 field1 value1 field2 value2，，，， ---- 一次设置多个键值对
4. hmget hash2 field1 field2，，，， ---- 获取多个filed对应的值
5. hgetall hash2  ---- 获取hash2中所有的键值对
6. hdel hash2 field1，field，，， ---- 删除一个或多个field
7. hexists hash2 field  ---- 存在返回1，不存在0
8. hlen hash2  ---- 返回key中包含的field数量
9. hkeys hash2 ---- 获取所有字段
10. hvals hash2 ---- 获取所有的值

> 4 无序集合set类型  
1. sadd set1 a b c d a ---- 插入（无重复）
2. smembers set1 ---- 获取set1中所有成员（无序）
3. srem set1 a ---- 删除指定元素
4. scard set1 ---- 返回集合元素的数量
5. srandmember set1  
    （默认随机返回一个元素，不删除）  
    （2 返回两个）  
6. smove set1 set2 b ---- 将set1中b删除并添加到另一个集合中
7. spop set1 1 ---- 随机移除一个元素
8. sismember set1 c ---- 判断c是否在集合中（1，0）
9. 集合的 并/交/差  
    sunion/sunionstore  
    sinter/sinterstore  
    sdiff/sdiffstore

> 5 有序集合set类类型  
1. zadd oset1 1 a 2 b 3 c 4 d 5 e 6 f ---- 添加元素
2. zrange oset1 0 -1 withscores  ---- 显示整个有序集合成员及成员的score值
3. zrangebyscore oset1 [min] [max] {withscores}
4. zscore oset1 a ---- 获取score
5. zrank oset1 a ---- 获取索引
6. zrevrank oset1 a ---- rev反向
7. zrem oset1 a b c ---- 删除元素
8. zremrangebyscore oset1 [min] [max]
9. zremrangebyrank oset1 [start] [end]
10. zcard oset1  ---- 统计
11. zcount oset1 [min] [max]  ---- 按score统计
12. 求交集并把score做聚合操作  
    zinterstore [newkey] [numberkeys] [key1] [key2] [key3] aggregate [sum/min/max]

> 6 基数统计HyperLogLog类型  
1. 它的一个引用场景：统计每天独立访问的ip数量  
    * 如果使用集合来记录，那么一个ip4个字节，数量过大后占用过多内存
    * 这个时候就需要HyperLogLog
2. HyperLogLog可以接收多个元素作为输入，并给出输入元素的基数估算值
    * 基数：集合中不同元素的数量
    * 估算值：算法给出的基数并不是精确的，但在合理的范围之内
3. 优点：
    * 即使输入元素的数量和体积非常庞大，计算基数所需要的空间总是固定的、且很小  
        redis里HyperLogLog只需要花费12kB内存就可以计算2^64个不同元素的基数
4. 缺点：
    * HyperLogLog只会根据输入元素来计算基数，而不会存储输入元素本身
5. 操作：
    * pfcount
    * pfadd
    * pfmerge

> 7 地理地图GEO类型  
1. geoadd
2. geodist
3. geohash ---- 重点  
    将地点的经纬度转化成一个二进制字符串（base32编码），经度单，纬度双  
    其实代表一个范围，这个特征可以用于附近地点搜索
4. geopos
5. georadius --- 以一个经纬度为中心
6. georadiusbymember ---- 以一个成员为中心

> 8 Stream类型（redis5.0）  
1. 强大的支持多播的可持久化的消息队列（借鉴Kafka的设计，后面再学习）  
    Redis Stream的结构有一个消息链表，将所有键入的消息都串起来，每个消息有唯一id，消息是可持久化的，重启redis内容还在
2. 基础命令：  
    * xadd stream_name id field-string[field-string....]  
        id : id一般使用 * 代替，表示服务器自动生成  
        field-string : 一系列的field-string代表消息内容
    * xlen stream_name  
        查看队列长度
    * xrange stream_name - +  
        获取消息列表所有消息 （-代表无穷小，+代表无穷大）
    * xdel stream_name id
    * del streanm_name
    * xread [count num] [block num] streams stream_name $(代表当前最大id)  
        该指令具有阻塞客户端做用 block 0代表阻塞时间无穷大

#### 健管理常用命令
1. dbsize
2. keys *
3. del == unlink
4. exists set k1
5. type k1

### Redis 多数据库
> Redis支持多个数据库，并且每个数据库的数据是隔离的不共享的  
> 并且基于单机才有，如果是集群就没有数据库的概念  
1. Redis是一个字典结构的服务器，实际上一个Redis实例提供了多个用来存储数据的字典（每个字典都理解成一个独立数据库）
2. 每个数据库对外都从0开始递增命名，Redis默认支持16个数据库（可更改databases）
3. 客户端与Redis建立连接后会自动选择0号数据库（使用select + num选择）  
    Redis不支持自定义数据库的名字，不支持为每个数据库设置不同的访问密码，多个数据库之间并不是完全隔离的（flushall清空所有数据）
4. 不同应用应该使用不同的redis实例存储数据

### 数据特征
> 使用位图（bitmap）  
> （也叫位数组，位向量）由bit组成的数组，Redis中的bitmap不是一种新的数据类型，实际上它的底层仍然是字符串，因为字符串本质上也是二进制大对象（Blob）  
> bitmap直接用bit位来保存数据，每一位所在的位置为偏移量，在bitmap上可执行AND、OR、OXR等位操作
1. 基本语法
    * setbit key offset value  
        offset值从0开始（从高位到低位）
    * bitcount key [start] [end] ---- 统计字符串被设置为1的bit数  
        start 和 end 代表的是字节（0 -1 所有字节）（0 0第一个字节）（不写 所有字节）
    * getbit key offset
    * bitop AND/OR/XOR/NOT newkey k1 k2 [k3...]  
        短的字符串所缺少的部分被看作0
2. bitmap的应用场景举例：  
    使用bitmap实现用户上线次数统计、活跃用户统计、上线天数等等
    * 一个用户号对应一个offset，活跃就setbit 1
    * 如果计算累计登录天数，则将每天的bitmap取AND操作
3. 内存使用情况：
    * 一千万用户也就1M多一点

> 设置键的存活时间  
1. 命令
    * expire key #s （常用） 设置
    * ttl key (s) 查看剩余存活时间
    * persist key 移除

> 使用sort命令  
> (前面所学的知识无法对集合排序)  
1. sort排序完整格式：  
    sort key [by pattern] [limit start count] [get pattern] [asc|desc] [alpha] [store dstkey]
2. 一般sort用法：  
    sort key [asc|desc]  (直接输出排序结果，只能比较数值，不改变原有list)
3. 使用alpha，对字符串进行排序
4. 使用limit限制返回结果
5. 使用外部key进行排序  
    BY选项：
        * 默认情况下sort uid直接按uid中的值排序
        * 通过BY选项，可以让uid按其他键的元素来排序  
            sort uid by user_level_*  ---- *这个占位符，取uid中的值  
    GET选项：
        * 可以根据排序的结果取出响应的键值  
            sort uid get user_level_* get # get user_name_*  
            (可以使用多个get，使用排序后的uid去获取其他键值，#代表自己)  
    组合使用BY和GET  
        * 不排序，但使用get功能，by + 不存在的键
6. 使用哈希表作为get和by的参数  
    例：  
    hmset user_info_1 name admin level 9999  
    hmset user_inf0_2 name jack level 9999  
    sort uid by user_info_*->level get user_info_*->name  
7. 保存结果store（保存的结果是什么类型呢？）  
    list类型

> 在Redis中使用管道  

redis是一种基于客户端-服务端模型以及请求/响应协议的tcp服务，着意味着必须经历一下过程：
1. 客户端向服务端发送一个查询请求，并监听Socket返回
2. 服务器接收客户端的请求命令，放入执行队列等待执行（Redis是单线程执行模式）
3. 执行命令，获取查询结果
4. 将结果返回客户端  
问题：  
    消耗时间：2，3取决于服务器，1，4取决于网络时延  
    如果将多条集中传给Redis服务器，这样就节省了很多网络时间  
    
做一个实验：
``` 
    # cat pipeline.txt  
    set mykey1  myvalue1  
    set mykey2  myvalue2  
    set mykey3  myvalue3  
    set mykey4  myvalue4  
    通过redis-cli --pipi选项发送管道命令  
    # cat pipeline.txt | /usr/local/redis/bin/redis-cli --pipe  
    All data transferred. Waiting for the last reply...  
    Last reply received from server.  
    errors: 0, replies: 4  
```

> Redis事务（了解）  
> （redis事务和关系型数据库的事务不是一回事，redis只是为了能够批量执行）  

1. 相关命令  
    * multi  --- 用于标记事务块的开始，redis会将后续的命令逐个放入队列中
    * exec  --- 执行先前放入队列的命令，然后恢复到正常连接状态
    * discard --- 清除先前放入队列的命令，然后恢复正常连接状态
2. 说明  
    * redis并不存在回滚操作  
    * 遇到错误会报错，还会继续执行之后的命令

> Redis的pub/sub 发布/订阅  
1. 简介  
    * 发布/订阅是一个经典的消息传递模式，在这个模式中，两种用户（发布者/订阅者）
    * 发布者：不是直接将信息发送给订阅者，而是将信息发送给频道
    * 然后由频道将信息转发给所有对这个频道感兴趣的订阅者
2. 实验
    * 第一个客户端subscribe channel1 --- 监听channel1，对channel1感兴趣（阻塞）
    * 第二个客户端subscribe channel1 channel2 --- 监听channel1，channel2
    * 第三个客户端publish channel “nmsl” --- 向channel1发送一条消息
    * pubsub channels 查看有哪些活跃的频道

> Lua脚本语言的基础使用  
1. 简介  
    * Lua是一种轻量小巧的脚本语言，用C语言编写（开源）
    * 设计目的是为了嵌入应用程序中，为应用程序提供灵活的扩展和定制功能
2. 安装Lua
    * http://www.lua.org/
3. 运行方式
    * 文件后缀.lua
    * lua xx.lua运行
4. 语法
    * 数据类型（8种）：
        * nil       ---  表示无效值，条件语句中表示false
        * boolean   ---  布尔
        * number    ---  双精度类型的实浮点苏
        * string    ---  单引号或双引号
        * userdata  ---  表示任意存储在变量中的c数据结构
        * function  ---  表示C或lua编写的函数
        * thread    ---  表示独立线程
        * table     ---  表示一个关联数组，数组索引可以是数字或字符串
    * 运算符
        * 赋值运算符
            * str = "hello".."world"  #通过..连接字符串
            * a,b,e = 1,2     # a = 1 b =2 e = nil
        * 算术运算符 
        * 关系运算符
            * ~=    # 不等于
        * 逻辑运算符
            * and or not
        * 其他
            * 返回字符串长度 #"lua"
    * 流程控制
        * if then else end
        * while（） do end
        * for a = 1,10,1 do end
        * repeat until()
    * 数组
        * arr = {1,2,3,4}
        * (#arr)返回数组长度
        * for i,v in ipairs(arr) do end
    * 函数
        * function calc(a,b,c)  return end
    * table
        * 类似Java中的map，JavaScript中的Json对象
        * person = {} person.name="jack" person.age=20
        * 取：person.name person["name"]
    * 模块和包
        * require "module" module是文件名
5. 如何在Redis里面玩Lua
    * eval "return redis.call('set', KEYS[1], ARGV[1])" 1 k1 v1  
        (简单的命令，但是这种方式并不适用)
    * 编辑一个Lua脚本，用于修改json格式数据的值  
     ```
          local id = KEYS[1]
          local data = ARGV[1]
          local dataSource = cjson.decode(data)  -- 把json格式的lua中的table对象
          
          local retjson = redis.call('get',id)  -- 调用redis的get命令
          
          if rejson==false then
                  rejson={}
          else
                  retjson=cjson.decode(retjson)
          end
          
          for k,v in pairs(dataSource) do
                  rejson[k] = v
          end
          redis.call('set',id,cjson.encode(retjson))
          return redis.call('get',id)
     ```
    * 执行上面的Lua脚本文件
        * /usr/local/redis/bin/redis-cli --eval ./updatejson.lua userKey , '{"username":"xiaobai" , "age":"18"}'
        * 逗号前后有空格
    * Lua调试模式
        * 在配置文件中配置 loglevel debug ； logfile "/usr/local/redis/log/redis.log"
        * 在Lua脚本中添加debug信息
            * redis.debug(id)
            * redis.log(redis.LOG_WARNING, cjson.encode(retjson))  -- 将信息写入日志文件
        * 使用 --ldb 选项开启Lua调试
            * /usr/local/redis/bin/redis-cli --ldb --eval ./updatejson.lua userKey , '{"username":"xiaobai" , "age":"18"}'
            * 键入s，单步执行
            * 键入w，浏览所有脚本
            * 键入b + 行号，设置一个断点
            * 键入c，直接运行到断点处停下

### Java使用Redis
> Jedis --- Redis官方推荐Java连接开发工具
1. Java单实例连接 --- JedisTest.java
    * 连接阿里云主机，首先得将6379端口加入到安全组
2. Jedis连接池连接 --- JedisPoolTest.java
3. 编写工具类 --- JedisPoolUtil.java
    
# 轻量级工作平台
## Day1
1. 利用spring boot初始化工具初始化所需框架和依赖
2. 利用navicat初始化MySql连接，并生成初始数据库
3. 修改application.yml文件，连接数据库
4. 编写Result文件，统一后端向前端的抛出文件
5. 编写实体User，实际含义是利用java操作在连接的数据库中建立User表
6. 编写UserRepository文件，该文件利用Spring框架的Bean库，仅仅需要声明接口，自动实现类。但是方法名必须按照固定格式，比如Find..By
7. 编写UserService文件,该文件作为Service的接口,统一Service的规范
8. 编写UserServiceimp1文件,具体实现UserService接口的功能

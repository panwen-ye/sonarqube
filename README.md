# sonarqube


1. 插件验证，在本地安装sonarscanner
sonar-scanner-cli-4.8.0.2856-windows.zip

在config目录下配置sonarqube的服务器地址及其他配置项

配置sonarscanner 命令到系统变量

2. 扫描规则实现如下：
context.addExtensions(JavaRulesDefinition.class, MySensor.class, CreateIssuesOnJavaFilesSensor.class);

3. sonarqube中需要激活规则

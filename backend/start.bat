@echo off
echo Starting Insight Flow Application...

REM 设置JVM参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

REM 启动应用
java %JAVA_OPTS% -jar target/insight-flow-0.0.1-SNAPSHOT.jar

pause 
 
# A demo maven plugin

---

# WHAT CAN I DO
Do the following at compile time
1. Custom output line prefix
2. Count lines
3. Check whether the naming meets the hump specification


# HOW TO USE:
You need to execute the following command line

1.Install plugin
```
cd demo-maven-plugin
mvn clean install
mvn me.demo:simple-maven-plugin:1.0-SNAPSHOT:buildinfo
```

2.compile project
```
cd maven-plugin-client
mvn compile
```


# A SIMPLE EXAMPLE

```
[INFO] --- maven-demo-plugin:1.0-SNAPSHOT:buildinfo (buildinfo) @ maven-plugin-client ---
[INFO] 
==========地址输出中================ 
Project build info:
[INFO]  我的前缀   /Users/caihui/Documents/project/demo-maven-plugin/maven-plugin-client/src/main/java
[INFO]  我的前缀   /Users/caihui/Documents/project/demo-maven-plugin/maven-plugin-client/src/test/java
[INFO] 
===========地址输出结束============
[INFO] 
===========代码行数计数中============
[INFO] /src/main/java:   33 lines of code in 2 files
[INFO] 
===========代码行数结束============
[INFO] 
===========检查字段驼峰中============
[info] 
开始校验/Users/caihui/Documents/project/demo-maven-plugin/maven-plugin-client/src/main/java/me/maven/demo/TestClass.java的变量命名是否符合驼峰规范
[info] 找到不符合驼峰命名的变量了:位于第11行，名为：TestOne
[info] 找到不符合驼峰命名的变量了:位于第12行，名为：TestTwo
[info] 找到不符合驼峰命名的变量了:位于第14行，名为：TestThree
[info] 
开始校验/Users/caihui/Documents/project/demo-maven-plugin/maven-plugin-client/src/main/java/me/maven/demo/App.java的变量命名是否符合驼峰规范
[info] 找到不符合驼峰命名的变量了:位于第12行，名为：TestOne
[info] 找到不符合驼峰命名的变量了:位于第13行，名为：TestTwo
[info] 找到不符合驼峰命名的变量了:位于第15行，名为：TestThree
[INFO] 
===========检查字段驼峰结束============
[INFO]
```


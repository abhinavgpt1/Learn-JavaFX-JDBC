#### [IMP] Q - dbconfigs doesn't exist in target when it's inside java, but do so when inside resources, why?
Ans - This is one of those classic Maven behaviors that trips everyone up at first!

The short answer is Convention over Configuration. Maven has a very strict, built-in idea of where different types of files belong. If you don't follow its default folder structure, it simply ignores the files it doesn't recognize.

Here is the breakdown of why this happens and how Maven treats both folders:

1. The src/main/java folder
- This folder is strictly reserved for Java Source Code (.java files).
- When you run a build, Maven hands this folder over to the Java Compiler (javac).
- The compiler looks for .java files, compiles them into .class files, and puts them in the target/classes folder.

Why your .json failed: The compiler doesn't know what a .json file is, so it completely ignores it. Because it isn't a compiled class, it never gets moved to the target folder.

2. The src/main/resources folder
- This folder is reserved for Non-Java files that your code needs at runtime (JSON configs, XML, images, properties files).
- Maven has a specific plugin dedicated to this called the maven-resources-plugin.
- By default, this plugin is programmed to look only inside src/main/resources.
- [PTR] It takes every file it finds there and does a direct copy over to target/classes so they are available on your project's classpath.

#### Q - How to make it work in the java folder?
A - You just need to explicitly tell Maven to look for resources inside your java folder by adding this to your pom.xml
```
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
            <include>**/*.json</include>
            </includes>
        </resource>
    </resources>
</build>
```

### Note
* The JavaFX jdbc apps don't mention database name. So, make sure to have database name in connection strings (dbconfigs).
* Check MySQLConnection.java: Why it isn't singleton, and why connection pool is better than legacy DriverManager.getConnection() way.
* qq: Does connection.close() really close the connection in a connection pool? 
    - Ans: No, it just returns it to the pool for reuse. This is one of the key benefits of using a connection pool: it allows you to reuse existing connections instead of creating new ones, which can be expensive in terms of time and resources.
    - A new connection is created only when:
        * Pool is not full yet: It creates new connections only until it reaches the maximum limit
        * All connections are busy: And pool allows expansion
        * A connection is truly dead: Timeout / network issue / invalid connection
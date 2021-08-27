# EasyFile

> 基于NIO文件管理工具集。

## spring boot使用

```java
<dependency>
     <groupId>com.wybusy</groupId>
     <artifactId>EasyFile</artifactId>
     <version>1.0-SNAPSHOT</version>
</dependency>
 ```

## TODO

- 对ZIP文件的操作


## 文件操作


##### - mkDir
若目录不存在，则新建各级目录

> param path    
> return Path

##### - lastModify
文件的最后修改时间

> param path    
> param fileName    
> return 毫秒时间戳

##### - dirTree
返回目录下所有文件及目录

> param path    
> param basePath    
> param filter ".{jpg,png,gif}"    
> param recursion 是否递归    
> return List< Map >

##### - read
读取文件内容

> param path    
> param fileName    
> param charset 编码方式，可选参数            
> return String

##### - write
把内容写入文件，若在linux中，把属性改为666

> param path    
> param fileName    
> param append 追加方式    
> param content    
> param charset 编码方式，可选参数            
> return boolean 成功与否

##### - copy
拷贝，若2,4参数为空，则为拷贝目录（其实没有用处，不会递归拷贝文件，只是新建了一个目录而已）

> param sourcePath    
> param sourceFile    
> param targetPath    
> param targetFile    
> return boolean

##### - move
移动与改名，若2，4参数为空，且在同一目录下，则为目录改名，否则目录不为空，就失败

> param sourcePath    
> param sourceFile    
> param targetPath    
> param targetFile    
> return boolean

##### - del
删除，若2参数为空，则删除和目录，但目录内有文件时，删除失败
> param path    
> param fileName    
> return boolean    



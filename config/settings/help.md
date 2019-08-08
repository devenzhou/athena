## 介绍
**settings** 目录下所有的文件为 **Intellij IDEA** 的配置文件

由于 **.zip** 文件属于二进制文件，git属于文本管理软件，
如果强行提交 **.zip** 格式的文件，就需要修改 **.gitignore** 文件，
将 **.gitignore** 文件中的 **.zip** 排除规则去掉，但这种规则是我们所不愿意看到的。

## 使用方式
1) 将 **settings** 文件使用压缩软件压缩为 **settins.zip** 的文件，该文件内部存储的就是 **settings** 目录下的内容。

2) 打开 **Intellij IDEA**， 选择 **File -> Import Settings** ,选择刚刚压缩的文件即可。
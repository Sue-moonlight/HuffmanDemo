# HuffmanDemo
哈夫曼算法demo，从创建哈夫曼到实现文件的压缩,完整的解读算法原理，编码解码过程
## 使用方法
### private static byte[] zip(byte[] bytes, Map<Byte, String> huffCodes) {}

传递参数：

byte[] 原始数据的byte格式

huffCodes 赫夫曼编码

其中huffCodes 可以使用 getCodes方法获得

本文的精髓在于 huffmanZip方法 & decode方法  

感兴趣的同学也下载demo试试～

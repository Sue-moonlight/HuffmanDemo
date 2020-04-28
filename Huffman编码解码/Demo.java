package Huffman编码解码;

import java.io.*;
import java.util.*;

public class Demo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        String msg = "can you can a can as a can";
//        byte[] bytes = msg.getBytes();
//        //进行赫夫曼编码
//        byte[] zipBytes = huffmanZip(bytes);
//       //使用赫夫曼编码进行解码
//        byte[] newBytes = decode(huffCodes,zipBytes);
//        //解码出来
////        System.out.println(new String(newBytes));
//        System.out.println(bytes.length);
//        System.out.println(zipBytes.length);

//        String src = "video.html";
//        String des = "2.zip";
//        try {
//            zipFile(src,des);
//        }catch (IOException e)
//        {
//        }

        unZip("2.zip","3.html");
    }

    private static void unZip(String src,String dst) throws IOException, ClassNotFoundException {
        FileInputStream is = new FileInputStream(src);
        ObjectInputStream ois = new ObjectInputStream(is);
        //读byte数组
        byte[] b = (byte[]) ois.readObject();
        //读赫夫曼编码表
        HashMap<Byte,String> codes = (HashMap<Byte,String>)ois.readObject();
        ois.close();
        is.close();
        byte[] bytes = decode(codes,b);

        FileOutputStream fileOutputStream = new FileOutputStream(dst);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    private static void zipFile(String src,String dst) throws IOException {
        FileInputStream is = new FileInputStream(src);
        byte[] b = new byte[is.available()];
        is.read(b);
        is.close();
        //使用赫夫曼编码
        byte[] byteZip = huffmanZip(b);
        FileOutputStream fs = new FileOutputStream(dst);
        ObjectOutputStream oos = new ObjectOutputStream(fs);
        oos.writeObject(byteZip);
        oos.writeObject(huffCodes);
        System.out.println(b.length);
        System.out.println(byteZip.length);

    }

    private static byte[] huffmanZip(byte[] bytes)
    {
        //统计byte出现的次数
        List<Node> nodes = getNodes(bytes);
        //创建赫夫曼树
        Node tree = creatHuffManTree(nodes);
        //创建赫夫曼编码表
        Map<Byte,String> huffCodes = getCodes(tree);
        //编码
        byte[] b = zip(bytes,huffCodes);
        return b;
    }

    /**
     * 进行赫夫曼编码
     * @param bytes
     * @param huffCodes
     * @return
     */
    private static byte[] zip(byte[] bytes, Map<Byte, String> huffCodes) {
        StringBuilder sb = new StringBuilder();
        //把需要压缩的byte数组处理成一个二进制字符串
        for (byte b:bytes)
        {
            sb.append(huffCodes.get(b));
        }
        //定义长度
        int len;
        if (sb.length()%8==0)
        {
            len = sb.length()/8;
        } else {
            len = sb.length()/8+1;
        }
        //用于存储压缩后的byte
        byte[]  by = new byte[len];
        //记录新的byte位置
        int index = 0;
        for (int i=0;i<sb.length();i+=8)
        {
            String strByte;
            if (i+8>sb.length())
            {
                strByte =sb.substring(i);
            }
            else
            {
                strByte = sb.substring(i,i+8);
            }
            byte byt = (byte) Integer.parseInt(strByte,2);
            by[index] = byt;
            index ++;
        }
        return by;
    }

    //用于临时存储路径
    static StringBuilder sb = new StringBuilder();
    //存储huff编码
    static  HashMap<Byte,String> huffCodes = new HashMap<>();
    /**
     * 根据赫夫曼树获取赫夫曼编码
     * @param tree
     * @return
     */
    private static Map<Byte, String> getCodes(Node tree) {
        if (tree == null)
        {
            return null;
        }
        //从树的顶部开始向下拼接（左边走为0，右边走为1）
        getCodes(tree.left,"0",sb);
        getCodes(tree.right,"1",sb);
        return huffCodes;
    }

    /**
     *
     * @param node
     * @param code  编码0或者1
     * @param sb
     */
    private static void getCodes(Node node, String code, StringBuilder sb) {
        StringBuilder sb2 = new StringBuilder(sb);
        sb2.append(code);

        if (node.data == null)
        {
            //data为null 说明不是叶子节点
            getCodes(node.left,"0",sb2);
            getCodes(node.right,"1",sb2);
        }
        else
        {
            //叶子节点，直接把节点存到map里
            huffCodes.put(node.data,sb2.toString());
        }

    }


    /**
     * 创建赫夫曼树
     * @param nodes
     * @return
     */
    private static Node creatHuffManTree(List<Node> nodes) {
        while (nodes.size() > 1)
        {
            //排序
            Collections.sort(nodes);
            //取出两个权值最低的二叉树
            Node left = nodes.get(nodes.size() -1);
            Node right = nodes.get(nodes.size()-2);
            //创建新的二叉树
            Node parent = new Node(null,left.weight+right.weight);
            //把之前的两个二叉树设置为新二叉树的子树
            //将两个树设置为新二叉树的子树
            parent.left = left;
            parent.right = right;
            //取出来的二叉树移除
            nodes.remove(left);
            nodes.remove(right);
            // 将新的树加到nodes
            nodes.add(parent);
        }
        return nodes.get(0);
    }

    /**
     * 把byte数组转node集合
     * @param bytes
     * @return
     */
    private static List<Node> getNodes(byte[] bytes) {
        List<Node> nodes = new ArrayList<>();
        Map<Byte,Integer> counts = new HashMap<>();
        for (byte b : bytes)
        {
            Integer count = counts.get(b);
            if (count== null)
            {
                counts.put(b,1);
            }
            else
            {
                counts.put(b,count+1);
            }
        }
        //将每一个键值对转为一个node对象
        for (Map.Entry<Byte,Integer> entry :counts.entrySet())
        {
            nodes.add(new Node(entry.getKey(),entry.getValue()));
        }
        return nodes;
    }

    /**
     * 使用赫夫曼编码表进行解码
     * @param huffCodes
     * @param bytes
     * @return
     */
    private static byte[] decode(HashMap<Byte, String> huffCodes, byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        //将byte数组转为二进制字符串
        for (int i=0;i<bytes.length;i++)
        {
            byte b = bytes[i];
            boolean flag = (i!=bytes.length-1);
            sb.append(byteToBitStr(flag,b));
        }
        // 把字符串安卓指定的赫夫曼编码进行解码
        //把赫夫曼编码的键值进行调换
        HashMap<String,Byte> map = new HashMap<>();
        for (Map.Entry<Byte,String > entry : huffCodes.entrySet())
        {
            map.put(entry.getValue(),entry.getKey());
        }
        //创建一个集合用于存byte
        List<Byte> list = new ArrayList<>();
        //处理字符串
        for (int i = 0;i<sb.length();)
        {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            //截取出一个byte
            while (flag)
            {
                String key = sb.substring(i,i+count);
                b = map.get(key);
                if (b==null)
                {
                    count++;
                }else
                {
                    flag = false;
                }
            }
            list.add(b);
            i+=count;
        }
        //集合转数组
        byte[] b = new byte[list.size()];
        for (int i = 0;i<b.length;i++)
        {
            b[i] = list.get(i);
        }
        return b;
    }

    /**
     *
     * @param flag  是否要补位数（bytes数组最后一个不需要补）
     * @param b
     * @return
     */
    private static String byteToBitStr(boolean flag,byte b)
    {
        int tmp = b;
        //按位或  取最后8个数字
        if (flag)
        {
            tmp |= 256;
        }
        String str = Integer.toBinaryString(tmp);
        if (flag)
        {
            return str.substring(str.length()-8);
        }
        else
        {
            return str;
        }
    }
}

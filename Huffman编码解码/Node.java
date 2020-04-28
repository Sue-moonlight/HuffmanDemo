package Huffman编码解码;

import com.sun.istack.internal.Nullable;

public class Node implements Comparable<Node>{
    //权值
    int weight;
    Node left;
    Node right;
    //存的data字符
    Byte data;

    public Node(Byte data, int weight) {
        this.weight = weight;
        this.data = data;
    }

    @Override
    public int compareTo(Node o) {
        return o.weight - this.weight;
    }

    @Override
    public String toString() {
        return "[ weight="+weight +" data=" + data +"]";
    }
}

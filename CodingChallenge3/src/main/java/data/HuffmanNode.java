package data;

public class HuffmanNode {

    private int weight;
    private Character value;

    private HuffmanNode left;
    private HuffmanNode right;

    private boolean leafNode;

    public HuffmanNode(int weight, HuffmanNode left, HuffmanNode right) {
        this.weight = weight;
        this.left = left;
        this.right = right;
        this.leafNode = false;
    }

    public HuffmanNode(int weight, Character value) {
        this.weight = weight;
        this.value = value;
        this.leafNode = true;
    }

    public boolean isLeafNode() {
        return this.leafNode;
    }

    public int getWeight() {
        return this.weight;
    }

    public Character getValue() {
        return this.value;
    }

    public HuffmanNode getLeft() {
        return this.left;
    }

    public HuffmanNode getRight() {
        return this.right;
    }
}

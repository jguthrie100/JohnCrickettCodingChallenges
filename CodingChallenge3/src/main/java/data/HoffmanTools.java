package data;

import java.util.*;

public class HoffmanTools {

    public Map<Character, Integer> buildFrequencyMap(StringBuilder text) {
        Map<Character, Integer> output = new HashMap<>();

        text.chars().forEach(c -> {
            int charFrequency = output.getOrDefault((char) c, 0);
            output.put((char) c, ++charFrequency);
        });

        return output;
    }

    public HuffmanNode buildFrequencyTree(Map<Character, Integer> frequencyMap) {
        TreeSet<HuffmanNode> frequencyTree = new TreeSet<>(Comparator.comparingInt(HuffmanNode::getWeight));

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getValue(), entry.getKey());

            frequencyTree.add(node);
        }

        while (frequencyTree.size() > 1) {
            HuffmanNode left = frequencyTree.pollFirst();
            HuffmanNode right = frequencyTree.pollFirst();

            frequencyTree.add(new HuffmanNode(left.getWeight() + right.getWeight(), left, right));
        }

        return frequencyTree.pollFirst();
    }

    public Map<Character, String> buildCodeTable(HuffmanNode root, String prefix) {
        Map<Character, String> codeTable = new HashMap<>();

        if (root.isLeafNode()) {
            codeTable.put(root.getValue(), prefix);

            return codeTable;
        } else {
            codeTable.putAll(buildCodeTable(root.getLeft(), prefix + "0"));
            codeTable.putAll(buildCodeTable(root.getRight(), prefix + "1"));
        }

        return codeTable;
    }
}

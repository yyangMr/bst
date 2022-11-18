package com.example.bst.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BSTree {
    @Data
    class Node {
        int key;
        Node left, right;
        private int lineNum = 0;
        private int leftLineNum = -1;
        private int rightLineNum = -1;

        public Node(int data) {
            key = data;
            left = right = null;
        }

        @Override
        public String toString() {
            return lineNum + " " + key + " " + leftLineNum + " " + rightLineNum + "\r\n";
        }

        public Node(String str) {
            String strs[] = str.split(" ");
            key = Integer.valueOf(strs[1]);
            lineNum = Integer.valueOf(strs[0]);
            leftLineNum = Integer.valueOf(strs[2]);
            rightLineNum = Integer.valueOf(strs[3]);
        }
    }

    List<Node> nodeList = new ArrayList<>();

    Node root;

    BSTree() {
        root = null;
    }

    void insert(int key) {
        root = insert_Recursive(root, key);
    }

    Node insert_Recursive(Node root, int key) {
        if (root == null) {
            root = new Node(key);
            root.lineNum = nodeList.size() + 1;
            nodeList.add(root);
            return root;
        }
        if (key < root.key) {
            root.left = insert_Recursive(root.left, key);
            root.leftLineNum = root.left.lineNum;
        } else if (key > root.key) {
            root.right = insert_Recursive(root.right, key);
            root.rightLineNum = root.right.lineNum;
        }
        return root;
    }


    boolean save(String filePath) {
        try {
            File writename = new File(filePath);
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            String bst = "lineNum data lNum rNum\r\n";
            for (Node node : nodeList) {
                bst += node.toString();
            }
            out.write(bst);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("写入文件异常----{}-{}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    BSTree(String filePath) {
        try {
            File filename = new File(filePath);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line != null && !line.isEmpty()) {
                    nodeList.add(new Node(line));
                }
            }
        } catch (Exception e) {
            log.error("读取文件异常----{}-{}", e.getMessage(), e);
        }
        Map<Integer, Node> map = nodeList.stream().collect(Collectors.toMap(Node::getLineNum, x -> x));
        root = mapToNode(1, map);
    }

    Node mapToNode(Integer index, Map<Integer, Node> map) {
        Node node = map.get(index);
        if (node == null) return null;
        if (node.leftLineNum != -1 || node.rightLineNum != -1) {
            node.left = mapToNode(node.leftLineNum, map);
            node.right = mapToNode(node.rightLineNum, map);
        }
        return node;
    }
}

@Slf4j
class Main {
    public static void main(String[] args) {
        BSTree bst = new BSTree();
        /*
              45
           /     \
          10      90
         /  \    /
        7   12  50   */
        bst.insert(45);
        bst.insert(10);
        bst.insert(7);
        bst.insert(12);
        bst.insert(90);
        bst.insert(50);

        // 保存树结构到文件
        String filePath = "./root.txt";
        bst.save(filePath);

        // 从文件中读取树结构
        BSTree bstFromFile = new BSTree(filePath);
        log.info("---------");
    }
}


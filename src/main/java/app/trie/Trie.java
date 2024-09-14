package app.trie;

import java.util.ArrayList;
import java.util.Scanner;

import app.database.DictionaryDatabase;
import javafx.util.Pair;

public class Trie {
    private static TrieNode root = new TrieNode();

    public static void insertWord(int wordId, String word) {
        if (root == null) {
            root = new TrieNode();
        }
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (current.getChild(ch) == null) {
                current.addChild(ch);
            }
            current = current.getChild(ch);
        }
        if (!current.isEndOfWord()) {
            current.setEndOfWord(true);
            current.setWord(wordId, word);
        }
    }

    private static TrieNode getEndNode(String word) {
        if (root == null) {
            return null;
        }
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.getChild(ch);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public static void removeWord(String word) {
        removeHelper(root, word, 0);
    }

    private static boolean removeHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            current.setWord(-1, null);
            return current.getAllChilds().isEmpty();
        }
        char character = word.charAt(index);
        TrieNode node = current.getChild(character);
        if (node == null) {
            return false;
        }

        boolean shouldDeleteChild = removeHelper(node, word, index + 1);

        if (shouldDeleteChild) {
            current.getAllChilds().remove(character);
            return current.getAllChilds().isEmpty() && !current.isEndOfWord();
        }
        return false;
    }

    public static ArrayList<Pair<Integer, String>> getAllWordStartWith(String prefix) {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        TrieNode current = getEndNode(prefix);
        if (current == null) {
            return list;
        }
        collectAllWord(current, list);
        return list;
    }

    private static void collectAllWord(TrieNode current, ArrayList<Pair<Integer, String>> list) {
        if (current == null) {
            return;
        }
        if (current.isEndOfWord()) {
            list.add(current.getWord());
        }
        for (char ch : current.getAllChilds().keySet()) {
            collectAllWord(current.getChild(ch), list);
        }
    }

    public static Pair<Integer, String> getWord(String word){
        TrieNode current = getEndNode(word);
        if (current == null || !current.isEndOfWord()){
            return null;
        }
        return current.getWord();
    }

    public static void main(String[] args) {
        //Test
        DictionaryDatabase.loadData();
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter prefix: ");
            String prefix = sc.nextLine();
              
            ArrayList<Pair<Integer, String>> vocabList = getAllWordStartWith(prefix);
            if (vocabList.isEmpty()) {
                System.out.println("No words found with the given prefix.");
            }
            else {
                for (Pair<Integer, String> e : vocabList) {
                        System.out.println(e.getValue());
                }
            }
        }
    }
}

package app.trie;

import java.util.ArrayList;

import javafx.util.Pair;

public class Trie {
    private TrieNode root;
    private int totalWords;

    public Trie() {
        this.root = new TrieNode();
        this.totalWords = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public TrieNode getRoot() {
        return root;
    }

    public void insertWord(int wordId, String word) {
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
            totalWords++;
        }
    }

    private TrieNode getEndNode(String word) {
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

    public void removeVocabulary(String word) {
        removeHelper(this.root, word, 0);
    }

    private boolean removeHelper(TrieNode current, String word, int index) {
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

    public ArrayList<Pair<Integer, String>> getAllVocabStartWith(String prefix) {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        TrieNode current = getEndNode(prefix);
        if (current == null) {
            return list;
        }
        collectAllVocab(current, list);
        return list;
    }

    private void collectAllVocab(TrieNode current, ArrayList<Pair<Integer, String>> list) {
        if (current == null) {
            return;
        }
        if (current.isEndOfWord()) {
            list.add(current.getWord());
        }
        for (char ch : current.getAllChilds().keySet()) {
            collectAllVocab(current.getChild(ch), list);
        }
    }

    public Pair<Integer, String> getWord(String word){
        TrieNode current = getEndNode(word);
        if (current == null || !current.isEndOfWord()){
            return null;
        }
        return current.getWord();
    }
}

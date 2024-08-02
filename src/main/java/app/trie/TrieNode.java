package app.trie;

import java.util.HashMap;
import javafx.util.Pair;

public class TrieNode {
    private HashMap<Character, TrieNode> childs;
    private boolean isWord;
    private Pair<Integer, String> word;

    public TrieNode(){
        this.childs = new HashMap<>();
        this.isWord = false;
        this.word = null;
    }


    public void addChild(Character character){
        if (childs.get(character) == null){
            childs.put(character, new TrieNode());
        }
    }

    public TrieNode getChild(Character character){
        return childs.get(character);
    }

    public void setEndOfWord(boolean isWord){
        this.isWord = isWord;
    }

    public boolean isEndOfWord(){
        return isWord;
    }

    public void setWord(int wordId, String word){
        if(wordId == -1 || word == null){
            this.word = null;
        }
        else{
            this.word = new Pair<>(wordId, word);
        }
    }

    public Pair<Integer, String> getWord(){
        return word;
    }

    public HashMap<Character, TrieNode> getAllChilds(){
        return childs;
    }
}
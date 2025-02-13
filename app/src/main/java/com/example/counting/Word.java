package com.example.counting;

public class Word {
    private String word;
    private int count;
    public Word(String word, int count){

        this.word = word;
        this.count = count;
    }

    public String getWord(){
        return word;
    }

    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public String toString(){
        return word.toLowerCase() + " (" + count + ")";
    }
}

package com.example.counting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


public class MainActivity extends AppCompatActivity {

    private EditText fileName;
    private Button calculate;
    private TextView answer;
    private Button wordCount;
    private Button sentenceCount;
    private Button uniqueWords;
    private Button readingAssessment;

    private void readFile() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fileName = (EditText) findViewById(R.id.editTextText);
        calculate = (Button) findViewById(R.id.button4);
        answer = (TextView) findViewById(R.id.textView4);
        wordCount = (Button) findViewById(R.id.wordCount);
        sentenceCount = (Button) findViewById(R.id.sentenceCount);
        uniqueWords = (Button) findViewById(R.id.uniqueWords);
        readingAssessment = (Button) findViewById(R.id.readingAssessment);


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenFileName = fileName.getText().toString().substring(17);
                ArrayList<Word> words = new ArrayList<Word>();
                ArrayList<String> commonWords = new ArrayList<String>(0);

                commonWords = populateCommonWords();

                if (chosenFileName.substring(chosenFileName.length() - 3).equals("txt")) {
                    Scanner scanner2 = null;
                    try {
                        scanner2 = new Scanner(getAssets().open(chosenFileName));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    while (scanner2.hasNextLine()) {

                        String regex = "[!._,'@? ]";
                        String[] lineWords = scanner2.nextLine().split(regex);
                        processTopFiveWords(lineWords, words, commonWords);

                    }
                }

                else if (chosenFileName.substring(chosenFileName.length() - 3).equals("pdf")) {
                    String regex = "[!._,'@? ]";
                    String[] lines = readPDF(chosenFileName).split(regex);

                    for (String line : lines) {
                        String[] lineWords = line.split(regex);
                        processTopFiveWords(lineWords, words, commonWords);
                    }
                }
                bubbleSort(words);
                answer.setTextSize(20);
                answer.setText("The top 5 most common words are: " + words.get(0) + ", " + words.get(1) + ", " + words.get(2) + ", " + words.get(3) + ", and " + words.get(4));
            }
        });


        wordCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setTextSize(25);
                answer.setText("The word count is: " + calculateWordCount());
            }
        });

        sentenceCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setTextSize(25);
                answer.setText("The sentence count is: " + calculateSentenceCount());
            }
        });

        readingAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String level;
                int count = calculateWordCount() / calculateSentenceCount();
                if (count <= 8) {
                    level = "Beginner";
                } else if (count <= 15) {
                    level = "Intermediate";
                } else {
                    level = "Advanced";
                }
                answer.setTextSize(20);
                answer.setText("The average number of words in a sentence is: " + count + ". Therefore, this is a " + level + " level text");
            }

        });


        uniqueWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenFileName = fileName.getText().toString().substring(17);
                ArrayList<Word> words = new ArrayList<Word>();
                ArrayList<String> commonWords = populateCommonWords();

                if (chosenFileName.substring(chosenFileName.length() - 3).equals("txt")) {
                    Scanner scanner2 = null;
                    try {
                        scanner2 = new Scanner(getAssets().open(chosenFileName));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    processUniqueWords(scanner2, words, commonWords);

                } else if (chosenFileName.substring(chosenFileName.length() - 3).equals("pdf")) {
                    PdfReader reader = null;
                    String content = "";
                    try {
                        reader = new PdfReader(getAssets().open(chosenFileName));
                        int n = reader.getNumberOfPages();
                        for (int i = 0; i < n; i++) {
                            content += PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                        }
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Scanner scanner2 = new Scanner(content);
                    processUniqueWords(scanner2, words, commonWords);
                }

                StringBuilder uniqueWordsBuilder = new StringBuilder();
                int displayLimit = Math.min(words.size(), 25);
                for (int i = 0; i < displayLimit; i++) {
                    uniqueWordsBuilder.append(words.get(i).getWord()).append(" ");
                }

                int numUniqueWords = words.size();
                answer.setTextSize(12);
                answer.setText("There are " + numUniqueWords + " unique words. The first 25 unique words are: " + uniqueWordsBuilder.toString().trim());
            }

            private void processUniqueWords(Scanner scanner, ArrayList<Word> words, ArrayList<String> commonWords) {
                while (scanner.hasNextLine()) {
                    String regex = "[!._,'@? ]";
                    String[] line = scanner.nextLine().split(regex);

                    for (String word : line) {
                        if (word.isEmpty()) continue;

                        boolean wordExists = false;
                        boolean wordCommon = false;
                        Word newWord = null;

                        //check if the word is already in the arraylist
                        for (int i = 0; i < words.size(); i++) {
                            if (word.toLowerCase().equals(words.get(i).getWord().toLowerCase())) {
                                wordExists = true;
                            }
                        }

                        //check if the word is a common word
                        for (int i = 0; i < commonWords.size(); i++) {
                            if (word.toLowerCase().equals(commonWords.get(i).toLowerCase())) {
                                wordCommon = true;
                            }
                        }

                        //if the word is uncommon and it hasn't already been added to the list of words
                        if (!wordExists && !wordCommon) {
                            newWord = new Word(word, 1);
                            words.add(newWord);
                        }

                    }
                }
            }
        });


    }

    public ArrayList<String> populateCommonWords() {
        ArrayList<String> commonWords = new ArrayList<String>(0);
        Scanner scanner1 = null;
        try {
            scanner1 = new Scanner(getAssets().open("commonWords.txt"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (scanner1.hasNextLine()) {
            commonWords.add(scanner1.nextLine());
        }
        return commonWords;
    }

    public int calculateSentenceCount() {
        int count = 0;
        String chosenFileName = fileName.getText().toString().substring(17);
        if (chosenFileName.substring(chosenFileName.length() - 3).equals("txt")) {
            Scanner scanner2 = null;
            try {
                scanner2 = new Scanner(getAssets().open(chosenFileName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (scanner2.hasNextLine()) {
                String regex = "[.!?]+\\s*";
                String[] line = scanner2.nextLine().split(regex);
                count += line.length;
            }
        } else if (chosenFileName.substring(chosenFileName.length() - 3).equals("pdf")) {
            String regex = "[.!?]+\\s*";
            String[] sentences = readPDF(chosenFileName).split(regex);
            count = sentences.length;
        }

        return count;
    }

    public int calculateWordCount() {
        int count = 0;
        String chosenFileName = fileName.getText().toString().substring(17);

        if (chosenFileName.substring(chosenFileName.length() - 3).equals("txt")) {
            Scanner scanner2 = null;
            try {
                scanner2 = new Scanner(getAssets().open(chosenFileName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (scanner2.hasNextLine()) {
                String regex = "\\s+";
                String[] line = scanner2.nextLine().split(regex);
                for (String word : line) {
                    count++;
                }
            }
        } else if (chosenFileName.substring(chosenFileName.length() - 3).equals("pdf")) {
            String regex = "\\s+";
            String[] words = readPDF(chosenFileName).split(regex);
            count = words.length;
        }
        return count;
    }

    public String readPDF(String chosenFileName) {
        PdfReader reader = null;
        String content = "";
        try {
            reader = new PdfReader(getAssets().open(chosenFileName));
            int n = reader.getNumberOfPages();
            for (int i = 0; i < n; i++) {
                content += PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public void bubbleSort(ArrayList<Word> words) {
        Word temp;
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < words.size() - 1; j++) {
                if (words.get(j).getCount() < (words.get(j + 1).getCount())) {
                    temp = words.get(j);
                    words.set(j, words.get(j + 1));
                    words.set(j + 1, temp);
                }
            }
        }

    }

    public void processTopFiveWords(String[] lineWords, ArrayList<Word> words, ArrayList<String> commonWords) {
        for (String word : lineWords) {
            if (word.isEmpty()) continue;

            boolean wordExists = false;
            boolean wordCommon = false;
            Word newWord = null;
            int index = 0;

            //check if the word is already in the arraylist
            for (int i = 0; i < words.size(); i++) {
                if (word.toLowerCase().equals(words.get(i).getWord().toLowerCase())) {
                    wordExists = true;
                    index = i;
                }
            }

            //check if the word is a common word
            for (int i = 0; i < commonWords.size(); i++) {
                if (word.toLowerCase().equals(commonWords.get(i).toLowerCase())) {
                    wordCommon = true;
                }
            }

            if (wordExists && !wordCommon) {
                words.get(index).setCount(words.get(index).getCount() + 1);
            } else if (!wordCommon) {
                newWord = new Word(word, 1);
                words.add(newWord);
            }
        }
    }
}


import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.util.*;

public class WordleHelper
{
    public static void main(String args [])
    {
        Scanner input = new Scanner(System.in);
        //Scanner for taking input

        Random rand = new Random();
        //Rand for selecting next valid word

        ArrayList<String> words = new ArrayList<String>();
        //Array list for storing possible words

        char containedChars[] = {'-','-','-','-','-'};
        //Stores characters that are definietly in the target word, '-' indicates unused space.

        char notContainedChars[] = new char[26];
        //Stores characters definietly not in the target word.

        char wordTemplate[] = {'-','-','-','-','-'};
        //Places any characters with known position in their index to try to create a template of the target word.

        char wrongPlace[] = {'-','-','-','-','-'};
        //Stores any characters that are in the word and the position where they CANT be.

        String currentWord = "crane";
        //Default starting word.

        boolean finish = false;
        //Check for exiting loop.
        int turnCounter = 0;
        //Stores number of turns.
        int containedCharsCounter = 0;
        //Stores number of contained characters, used for insertion into containedChars[]
        
        Dictionary dic = new Dictionary();
        //New dictionary object for loading in possible words.

        for(int i = 0; i< dic.getSize(); i++)
        {
            words.add(dic.getWord(i).trim());
            //Words from words.txt are loaded into arraylist words.
        }

        System.out.println("As your first word input: crane");
        //Same starting prompt everytime.

        while(finish == false)
        {
            if(turnCounter == 6)
            //If we have run out of turns exit the game
            {
                finish = true;
                break;
            }

            Arrays.fill(wrongPlace, '-');
            //Refills the wrongplace array with unused space chars as its data will have already been used.

            System.out.println("Enter the result you got back in the format 2-correct, 1-misplaced, 0-incorrect,all 3's-invalid word: ");

            String result1 = input.nextLine();
            //Takes in input word.

            if(result1.equals("22222"))
            //If word was correct exit programme.
            {
                finish = true;
                break;
            }
            else if(result1 == "33333")
            //If word was invalid, read in next word and continue to try to guess again.
            {
                int current = rand.nextInt(words.size());
                currentWord = words.get(current);

                System.out.println("Next try: " + currentWord);

                words.remove(current);

                continue;
            }

            for(int i = 0; i< result1.length(); i++)
            //Cycles through each number in result to interpret feedback.
            {
                if(result1.charAt(i) == '2')
                //If letter correct.
                {
                    wordTemplate[i] = currentWord.charAt(i);
                    //Add in at appropriate index.
                    boolean contains = false;
                    for (char y : containedChars) 
                    {
                        if (y == currentWord.charAt(i)) 
                        {
                            contains = true;
                            break;
                        }
                    }
                    //Loop above(97-105) checks if letter is already in containedChars
                    if(!contains)
                    //If it isnt contained, we add it in. Avoids double entries and array out of bounds error.
                    {
                        containedChars[containedCharsCounter] = currentWord.charAt(i);
                        containedCharsCounter++;
                    }
                }
                else if(result1.charAt(i) == '1')
                //If letter is misplaced.
                {
                    boolean contains = false;
                    for (char y : containedChars) 
                    {
                        if (y == currentWord.charAt(i)) 
                        {
                            contains = true;
                            break;
                        }
                    }
                    //Loop above(117-125) checks if letter is already in containedChars
                    if(!contains)
                    //If it isnt contained, we add it in. Avoids double entries and array out of bounds error.
                    {
                        containedChars[containedCharsCounter] = currentWord.charAt(i);
                        containedCharsCounter++;
                    }

                    wrongPlace[i] = currentWord.charAt(i);
                    //Stores the index in which the letter is definietly misplaced.
                }
                else
                //If the letter is not in word.
                {
                    boolean contains = false;
                    
                    for (char y : containedChars) 
                    {
                        if (y == currentWord.charAt(i)) 
                        {
                            contains = true;
                            break;
                        }
                    }
                    //Above loop(140-149) checks to see if letter isnt already confirmed as contained(ie tried double letter word, is actually single letter word.)
                    if(!contains)
                    //If it isnt already confirmed, add it into the not contained array.
                    {
                        notContainedChars[(int)currentWord.charAt(i) - 97] = currentWord.charAt(i);
                    }
                    
                }
            }

            for(int i = 0; i < 26; i++)
            //Removes all words that have letters we know the target word doesn't have.
            {
                pruneListContain(words, notContainedChars[i]);
            }
            for(int i = 0; i < containedChars.length; i++)
            //Removes all words that don't have the letters we know the target word has.
            {
                pruneListNoContain(words, containedChars[i]);
            }

            pruneListTemplate(words,wordTemplate);
            //Removes all words that dont match the known target template, ie.Removes any word that doesn't have letters at the correct indexes.

            pruneListWrongPlace(words,wrongPlace);
            //Removes all words that have the correct letters but in the wrong indexes, helps narrow down search faster.

            int current = 0;
            //Variable used for storing guessed index later.

            if(words.size() <= 0)
            //In case we run out of possible words in array list we print what info we have and let the user try to guess.
            {
                System.out.println("Sorry, out of recommendations!\nHere is what I have:");
                System.out.println("It roughly fits the template: ");
                for(int i = 0; i<5; i++)
                {
                    System.out.print(wordTemplate[i]);
                }
                System.out.println("\nAnd it definietly contains the letters: ");
                for(int i = 0; i<5; i++)
                {
                    System.out.print(containedChars[i]);
                }

                System.out.println("\nGood Luck!");

                break;
            }
            else
            {
                current = rand.nextInt(words.size());
                //Selects a random index based on the available size.
                currentWord = words.get(current);
                //Selects a random word from the possible words.
            }

            System.out.println("Next try: " + currentWord);

            words.remove(current);
            //Removes current word from possible words.

            turnCounter++;
            //Increments turn counter.

        }

        input.close();
        //Close scanner to avoid memory leak.
    }

    static void pruneListContain(ArrayList<String> words, char c)
    //Method removes any words from the given array list that contain the char c
    {
        for(int i = 0; i<words.size();i++)
        {
            if((words.get(i).contains(String.valueOf(c))))
            {
                words.remove(i);
                i--;
            }
        }
    }

    static void pruneListNoContain(ArrayList<String> words, char c)
    //Method removes any words that dont contain the char c if the char c isnt a unused space '-' character.
    {
        for(int i = 0; i<words.size();i++)
        {
            if(words.get(i).contains(String.valueOf(c)) == false && c != '-')
            {
                words.remove(i);
                i--;
            }
        }
    }

    static void pruneListTemplate(ArrayList<String> words, char wordTemplate[])
    //Method compares each word in the list to the template, if even one character that isn't the unused space character '-' doesnt match it removes the whole word.
    {
        for(int i = 0; i<5; i++)
        {
            if(wordTemplate[i] == '-')
            {
                continue;
            }
            for(int x = 0; x<words.size(); x++)
            {
                if(words.get(x).charAt(i) != wordTemplate[i])
                {
                    words.remove(x);
                x--;
                }
            }
        }
    }
    static void pruneListWrongPlace(ArrayList<String> words, char wordTemplate[])
    //Method compares each word in the list to the incorrect index array. If word has even one character that is in the index we know the letter isnt, it removes the whole word.
    {
        for(int i = 0; i<5; i++)
        {
            if(wordTemplate[i] == '-')
            {
                continue;
            }
            for(int x = 0; x<words.size(); x++)
            {
                if(words.get(x).charAt(i) == wordTemplate[i])
                {
                    words.remove(x);
                    x--;
                }
            }
        }
    }
}

class Dictionary{
    //Dictionary class for reading in txt.
     
    private String input[]; 

    public Dictionary(){
        input = load("C://words.txt");  
    }
    
    public int getSize(){
        return input.length;
    }
    
    public String getWord(int n){
        return input[n];
    }
    
    private String[] load(String file) {
        File aFile = new File(file);     
        StringBuffer contents = new StringBuffer();
        BufferedReader input = null;
        try {
            input = new BufferedReader( new FileReader(aFile) );
            String line = null; 
            int i = 0;
            while (( line = input.readLine()) != null){
                contents.append(line);
                i++;
                contents.append(System.getProperty("line.separator"));
            }
        }catch (FileNotFoundException ex){
            System.out.println("Can't find the file - are you sure the file is in this location: "+file);
            ex.printStackTrace();
        }catch (IOException ex){
            System.out.println("Input output exception while processing file");
            ex.printStackTrace();
        }finally{
            try {
                if (input!= null) {
                    input.close();
                }
            }catch (IOException ex){
                System.out.println("Input output exception while processing file");
                ex.printStackTrace();
            }
        }
        String[] array = contents.toString().split("\n");
        for(String s: array){
            s.trim();
        }
        return array;
    }
}
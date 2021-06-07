package sudoku.com.company;

import stev.booleans.PropositionalVariable;

import javax.swing.*;
import java.util.*;

public class GameTable {

    private char[][] Values;


    public GameTable(String value) {
        if (value.length() != 81){
            throw new IllegalStateException("Unvalid Game Table");
        }
        Values = new char[9][9];
        int pos = 0;
        for (int i =0; i<9; i++){
            for (int j=0; j<9; j++){
                Values[i][j]=value.charAt(pos);
                pos++;
            }
        }
    }



    //getters and setters

    public void set(int i, int j, char k){this.Values[i][j] = k;}
    public char[][] getValues() {
        return Values;
    }

    public void setValues(char[][] values) {
        Values = values;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder().append(" || ");
        int cpt = 1;
        int cptL = 1;

        for (int i =0; i<9; i++){
            for (int j=0; j<9; j++){
                if(Character.toString(Values[i][j]).equals("#")){
                    if(cpt < 3) {
                        str.append(" ").append(" | ");
                        cpt++;
                    }else{
                        str.append(" ").append(" || ");
                        cpt=1;
                    }
                }
                else {
                    if(cpt < 3) {
                        str.append(Values[i][j]).append(" | ");
                        cpt++;
                    }else{
                        str.append(Values[i][j]).append(" || ");
                        cpt=1;
                    }
                }
            }
            if(cptL == 3){
                str.append("\n =========================================\n || ");
                cptL=1;
            }else{
                str.append("\n -----------------------------------------\n || ");
                cptL++;
            }

        }
        return str.toString();
    }
}

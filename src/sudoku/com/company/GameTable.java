package sudoku.com.company;

import stev.booleans.PropositionalVariable;

import javax.swing.*;
import java.util.*;

public class GameTable {


    public boolean completed;

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


    /*
    Cette fonction permet de créer l'ensemble des variables propositionnelles
     */


    public Boolean caseIsValid(int x, int y, char val){
        System.out.println("Checking case "+x+", "+y +" value: " + Values[x][y]);

        if (checkSquare(x,y,val) && checkColumn(x,y, val) && checkRow(x,y,val)){
            return true;
        }
        return false;
    }



    private boolean checkSquare(int x, int y, char val) {
        if(Values[x][y] =='#'){
            return true;
        }
        int x_init = x-(x%3);
        int y_init = y-(y%3);
        for(int j = y_init; j< y_init+3; j++){
            for(int i = x_init; i< x_init+3; i++){
                if(i==x & j==y){
                    continue;
                }
                if((Values[i][j]==val)){
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkRow(int x, int y, char val) {
        int j = y;

        for(int i = 0; i<9; i++){
            if(i==x & j==y){
                continue;
            }
            if(Values[i][j]==val){
                return false;
            }
        }
        return true;
    }

    private boolean checkColumn(int x, int y, char val) {
        int i = x;

        for(int j =0 ; j< 9; j++){
            if(i==x & j==y){
                continue;
            }
            if(Values[i][j]==val){
                return false;
            }
        }
        return true;
    }

    public void AssignedSlots(ArrayList<String> AS){
        String coordinates = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!Character.toString(Values[i][j]).equals("#")){
                    coordinates = Integer.toString(i) + Integer.toString(j) + Character.toString(Values[i][j]);
                    AS.add(coordinates);
                }
            }
        }
    }


    //getters and setters

    public char get(int i, int j){return Values[i][j];}
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

package sudoku.com.company;

import jdk.swing.interop.SwingInterOpUtils;
import stev.booleans.*;

import javax.swing.*;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class Formulas {

    /*----------------------------- Attributs -------------------*/
    private ArrayList<BooleanFormula> formulas = new ArrayList<>();
    private static PropositionalVariable[][][] PropVars = new PropositionalVariable[9][9][9];

    // AlreadyAssigned contient les cases non vides
    private And AlreadyAssigned = null;

    // AllImplies représente les régles du sudoku et les interprétation de ces règle sous
    // forme de logique propositionnelle
    private ArrayList<Implies> AllImplies = new ArrayList<>();
    private And AllImpliesProcessed = null;

    // AllPossibleValues rerpésente que chaque case peut avoir un chiffre entre 1 et 9
    private And AllPossibleValues = null;

    public PropositionalVariable[][][] PossibleValuesOfSlots = new PropositionalVariable[9][9][9];
    public Not[][][] NotPossibleValuesOfSlots = new Not[9][9][9];

    /*--------------------------------------------------------------------*/

    public And getAllPossibleValues() {
        return AllPossibleValues;
    }

    public And getAlreadyAssigned() {
        return AlreadyAssigned;
    }

    public And getAllImpliesProcessed() {
        return AllImpliesProcessed;
    }

    public ArrayList<Implies> getAllImplies() {
        return AllImplies;
    }



    public Formulas(){
        this.VarsPropCreation();
    }

    public void VarsPropCreation(){
        String key = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    // On forme un string avec les indices pour une notation plus claire.
                    key = Integer.toString(i+1) + Integer.toString(j+1) + Integer.toString(k+1);
                    PropVars[i][j][k] = new PropositionalVariable(key);
                }
            }
        }
        // 9*9*9 variables -> 729 variables propositionnelles
    }

    public ArrayList<BooleanFormula> getFormulas(){return formulas;}

    public void setFormulas(ArrayList<BooleanFormula> formulas) {
        this.formulas = formulas;
    }

    /*
    public void AllPossibleValues(char[][] values){
        System.err.println("ALL POSSIBLE VALUES");
        And BigAnd, TempAnd;
        ArrayList<And> Ands = new ArrayList<>();
        ArrayList<BooleanFormula> Ors = new ArrayList<>(); // CHANGER ORS PAR ANDS
        Or[][] ValuesInSlot = new Or[9][9];
        Not[] allNotePossible = new Not[9];
        int cpt = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (Character.toString(values[i][j]).equals("#")) {
                    ValuesInSlot[i][j] = new Or(PossibleValuesOfSlots[i][j][0], PossibleValuesOfSlots[i][j][1], PossibleValuesOfSlots[i][j][2]
                            , PossibleValuesOfSlots[i][j][3], PossibleValuesOfSlots[i][j][4], PossibleValuesOfSlots[i][j][5]
                            , PossibleValuesOfSlots[i][j][6], PossibleValuesOfSlots[i][j][7], PossibleValuesOfSlots[i][j][8]);
                }else{
                    ValuesInSlot[i][j] = new Or(PropVars[i][j][Character.getNumericValue(values[i][j])-1]);//, PropVars[i][j][Character.getNumericValue(values[i][j])-1]);
                    TempAnd = new And(ValuesInSlot[i][j]);
                    for (int k = 0; k < 9; k++) {
                        if(k != Character.getNumericValue(values[i][j])-1){
                            TempAnd = new And(TempAnd, new Not(PropVars[i][j][k]));
                        }

                    }
                    Ands.add(TempAnd);

                }
                Ors.add(ValuesInSlot[i][j]);
            }

                /*ValuesInSlot[i][j] = new Or(PropVars[i][j][0], PropVars[i][j][1]);
                for (int k = 2; k < 9; k++) {
                    ValuesInSlot[i][j] = new Or(ValuesInSlot[i][j], PropVars[i][j][k]);
                }*/

                    /*ValuesInSlot[i][j] = new And(ValuesInSlot[i][j], NotPossibleValuesOfSlots[i][j][0], NotPossibleValuesOfSlots[i][j][1],  NotPossibleValuesOfSlots[i][j][2]
                            , NotPossibleValuesOfSlots[i][j][3], NotPossibleValuesOfSlots[i][j][4], NotPossibleValuesOfSlots[i][j][5]
                            , NotPossibleValuesOfSlots[i][j][6], NotPossibleValuesOfSlots[i][j][7],  NotPossibleValuesOfSlots[i][j][8]);*/
            //
/*
        BigAnd = new And(Ors.get(0), Ors.get(1));
        for (int i = 0; i < Ors.size(); i++) {
            BigAnd = new And(BigAnd, Ors.get(i));
        }
        for (int i = 0; i < Ands.size(); i++) {
            BigAnd = new And(BigAnd, Ands.get(i));
        }
        this.AllPossibleValues = BigAnd;
        System.out.println(" je suis le bigand valuesPossible" + AllPossibleValues);
    }

*/





    /*
     * Check table to add formulas
     */
    public void CheckTable(char[][] values) {
        //this.AlreadyAssigned(values);

        //Cette boucle ajoute les règle sur les cases vides
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!Character.toString(values[i][j]).equals("#")) {
                    this.AddAssignedSlots(i, j, Character.getNumericValue(values[i][j]));
                    //System.out.print(" En Ligne : ");
                    this.OneNumberPerLine(i, j, Character.getNumericValue(values[i][j]));
                    //System.out.print(" En Colonne : ");
                    this.OneNumberPerColumn(i, j, Character.getNumericValue(values[i][j]));
                    //System.out.print("Par sous-tableau : ");
                    this.OneNumberPerSector(i, j, Character.getNumericValue(values[i][j]));
                } else {
                    this.OneNumberPerSlots(i, j);
                }

            }


            // Cette boucle représente les règles ligne colonne sous tableaux appliqués à toutes les cases possibles.
        /*
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    this.OneNumberPerLine(i, j, k+1);
                    //System.out.print(" En Colonne : ");
                    this.OneNumberPerColumn(i, j, k+1);
                    //System.out.print("Par sous-tableau : ");
                    this.OneNumberPerSector(i, j, k+1);

                }
            }
        }

         */
        }
            And BigAnd = new And(AllImplies.get(0), AllImplies.get(1));
            for (int i = 2; i < AllImplies.size(); i++) {
                BigAnd = new And(BigAnd, AllImplies.get(i));
            }
            AllImpliesProcessed = BigAnd;
            System.err.println("------ All Implies ----------");
            System.err.println("implies : " + AllImpliesProcessed);
        }

    private void AddAssignedSlots(int I, int J, int K) {
        And BigAnd = new And(PropVars[I][J][K-1]);
        for (int k = 0; k < 9; k++) {
            if(k+1 != K){
                BigAnd = new And(BigAnd, new Not(PropVars[I][J][k]));
            }
        }
        formulas.add(BigAnd);
    }


    /*
     * One number per line
     @param I : Line of the assigned slot
     @param J : Column  "   "     "     "
     @param K : Value in the slot
     */
    private void OneNumberPerLine(int I, int J, int K){
        Not[] allNotLine = new Not[8]; // there are 8 slots per line that cannot be the same as the one in input
        int index = 0;
        for (int j = 0; j < 9; j++) {
            if(j != J){
                allNotLine[index] = new Not(PropVars[I][j][K-1]);
                index++;
            }
        }
        And Bigand = new And(allNotLine[0], allNotLine[1]);
        for (int i = 2; i < 8; i++) {
            Bigand = new And(Bigand, allNotLine[i]);
        }
        Implies implies = new Implies(PropVars[I][J][K-1], Bigand);
        //Implies inverse = new Implies( Bigand, PropVars[I][J][K-1]);
        formulas.add(implies);
        AllImplies.add(implies);
        //AllImplies.add(inverse);
        //System.out.println(Bigand);
    }

    /*
     * One number per Column
     @param I : Line of the assigned slot
     @param J : Column  "   "     "     "
     @param K : Value in the slot
     */
    private void OneNumberPerColumn(int I, int J, int K){
        Not[] allNotColumn = new Not[8]; // there are 8 slots per column that cannot be the same as the one in input
        int index = 0;
        for (int i = 0; i < 9; i++) {
            if(i != I){
                allNotColumn[index] = new Not(PropVars[i][J][K-1]);
                index++;
            }
        }
        And Bigand = new And(allNotColumn[0], allNotColumn[1]);
        for (int j = 2; j < 8; j++) {
            Bigand = new And(Bigand, allNotColumn[j]);
        }
        Implies implies = new Implies(PropVars[I][J][K-1], Bigand);
        //Implies inverse = new Implies(Bigand, PropVars[I][J][K-1]);
        //AllImplies.add(inverse);
        formulas.add(implies);
        AllImplies.add(implies);
        //System.out.println(Bigand);
    }

    /*
     * One number per sector
     @param I : Line of the assigned slot
     @param J : Column  "   "     "     "
     @param K : Value in the slot
     */
    public void OneNumberPerSector(int I, int J, int K){
        Not[] allNotSector = new Not[8];
        int index = 0;
        for (int i = (I - I%3); i <= (I + 2 - I%3); i++) {
            for (int j = (J - J%3); j <= (J + 2 - J%3); j++) {
                if((i != I) || (j != J)){
                    allNotSector[index] = new Not(PropVars[i][j][K-1]);
                    index++;
                }
            }
        }

        And Bigand = new And(allNotSector[0], allNotSector[1]);
        for (int k = 2; k < 8; k++) {
            Bigand = new And(Bigand, allNotSector[k]);
        }
        Implies implies = new Implies(PropVars[I][J][K-1], Bigand);
        //Implies inverse = new Implies(Bigand, PropVars[I][J][K-1]);
        //AllImplies.add(inverse);
        formulas.add(implies);
        AllImplies.add(implies);

    }

    /*
     * One slot contains 1 number between 1-9
     */
    private void OneNumberPerSlots(int I, int J){
        Or BigOr; // new Or(PropVars[0][0][0], PropVars[0][0][1]);
        /*for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {*/
        BigOr = new Or(PropVars[I][J][0], PropVars[I][J][1]);
        for (int k = 2; k < 9; k++) {
                    /*BigOr = new Or(PropVars[i][j][0], PropVars[i][j][1], PropVars[i][j][2]
                            , PropVars[i][j][3], PropVars[i][j][4], PropVars[i][j][5]
                            , PropVars[i][j][6], PropVars[i][j][7], PropVars[i][j][8]);*/
            BigOr = new Or(BigOr, PropVars[I][J][k]);
        }
        formulas.add(BigOr);



        //   }
        // }
    }


    public void AlreadyAssigned(char[][] values){
        System.err.println("------ALREADY ASSIGNED---------");
        ArrayList<PropositionalVariable> assignedSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!Character.toString(values[i][j]).equals("#")){
                    int indexValue = Character.getNumericValue(values[i][j]);
                    assignedSlots.add(PropVars[i][j][indexValue-1]);
                    for (int k = 0; k < 9; k++) {
                        if(Character.getNumericValue(values[i][j]) == k+1){
                            this.NotPossibleValuesOfSlots[i][j][k] = new Not(PropVars[i][j][k]);
                        }
                    }
                }
            }
        }
        And BigAnd = new And(assignedSlots.get(0), assignedSlots.get(1));
        for (int i = 2; i < assignedSlots.size(); i++) {
            BigAnd = new And(BigAnd, assignedSlots.get(i));
        }
        AlreadyAssigned = BigAnd;
        System.err.println(" Already Assinged : " + AlreadyAssigned);

    }





}

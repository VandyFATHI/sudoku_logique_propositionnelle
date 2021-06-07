package sudoku.com.company;

import jdk.swing.interop.SwingInterOpUtils;
import stev.booleans.*;

import javax.swing.*;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class Formulas {

    /*----------------------------- Attributs -------------------*/

    /* PropVars contient toutes les variables propositionnelles*/
    private static PropositionalVariable[][][] PropVars = new PropositionalVariable[9][9][9];

    /*--------------------------------------------------------------------*/




    public Formulas(){
        this.VarsPropCreation();
    }

    /*
    Cette fonction crée toutes les variables propositionnelles
    Une variable est représentée par : ijk
    où i = ligne de la case, j = colonne de la case, k = valeur dans la case
     */
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


    /*
       Pour la totalité des règles nous les avons exprimé selon une formulation équivalente.
     */

    /*
    Règle 1  : Chaque case ne peut contenir qu’un seul chiffre  <=> Une case peut contenir au moins un nombre ET Une case peut contenir au plus un nombre.
     */

   /* La fonction RuleAtLeastOne, retourne une formule de logique propositionnelle.
   Cette formule est And(Or(ijk)) avec i, j, k variant entre 1 et 9
    */
    public BooleanFormula RuleAtLeastOne() { // One slot can have at Least one number
        BooleanFormula[] allAtLeast = new BooleanFormula[9];
        int index = 0;

        ArrayList<BooleanFormula> AllRuleAtLeastOne = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                            allAtLeast[index] = PropVars[i][j][k];
                            index++;
                    }
                    index = 0;
                    Or or = new Or(allAtLeast[0], allAtLeast[1]);
                    for (int l = 2; l < 9; l++) {
                        or = new Or(or, allAtLeast[l]);
                    }
                    AllRuleAtLeastOne.add(or);
            }
        }
        return new And(AllRuleAtLeastOne);
    }

    /* La fonction RuleAtMost, retourne une formule de logique propositionnelle.
   Cette formule est And(Or(Not(ijk, ijm)) avec i, j, k et m variant entre 1 et 9 et m différent de k.
    */
    public BooleanFormula RuleAtMostOne() { // One slot can have at most one number
        int index = 0;

        ArrayList<BooleanFormula> AllAtMost = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    for (int l = 0; l < 9; l++) {
                        if(l != k) {
                            AllAtMost.add(new Or(new Not(PropVars[i][j][k]), new Not(PropVars[i][j][l])));
                            index++;
                            //System.out.println(new Or(new Not(PropVars[i][j][k]), new Not(PropVars[i][j][l])));
                        }
                    }
                    index = 0;
                }

            }
        }
        return new And(AllAtMost);
    }

    /*
    Après ces deux fonctions, on peut exprimer qu'une case ne peut contenir qu'une seule valeur
     */

    /*
    Règle 2 : Chaque chiffre doit apparaître exactement une fois dans chaque ligne de la grille <=> Tous les chiffes apparaissent sur une ligne
     */
    /* La fonction RuleTwo, retourne une formule de logique propositionnelle.
   Cette formule est And(Or(ijk)) avec i, j, k variant entre 1 et 9 . j variant en fonction de i et k fixéew
    */
    public BooleanFormula RuleTwo() { // All numbers are present in one line
        BooleanFormula[] allLine = new BooleanFormula[9];
        int index = 0;

        ArrayList<BooleanFormula> AllRule2 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    for (int l = 0; l < 9; l++) {
                        allLine[index] = PropVars[i][l][k];
                        index++;
                    }
                    index = 0;
                    Or or = new Or(allLine[0], allLine[1]);
                    for (int l = 2; l < 9; l++) {
                        or = new Or(or, allLine[l]);
                    }
                    //System.out.println(or);
                    AllRule2.add(or);
                }
            }
        }
        return new And(AllRule2);
    }

    /*
    Règle 3 : Chaque chiffre doit apparaître exactement une fois dans chaque colonne de la grille <=> Tous les chiffres apparaissent sur une colonne
     */
     /* La fonction RuleThree, retourne une formule de logique propositionnelle.
   Cette formule est And(Or(ijk)) avec i, j, k variant entre 1 et 9 . i variant en fonction de j et k fixées
    */
    public BooleanFormula RuleThree() { // One number per line
        BooleanFormula[] allColumn = new BooleanFormula[9]; // there are 8 slots per line that cannot be the same as the one in input
        int index = 0;

        ArrayList<BooleanFormula> AllRule3 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    for (int l = 0; l < 9; l++) {
                        allColumn[index] = PropVars[l][j][k];
                        index++;
                    }
                    index = 0;
                    Or or = new Or(allColumn[0], allColumn[1]);
                    for (int l = 2; l < 9; l++) {
                        or = new Or(or, allColumn[l]);
                    }
                    //System.out.println(or);
                    AllRule3.add(or);
                }
            }
        }

        return new And(AllRule3);
    }

    /*
    Règle 4 : Chaque chiffre doit apparaître exactement une fois dans chacune des neuf sous-grilles de taille 3×3 <=> Tous les chiffres apparaissent dans une
    sous-grille de taille 3x3
     */
    /* La fonction RuleFour, retourne une formule de logique propositionnelle.
   Cette formule est And(Or(ijk)) avec i, j, k variant entre 1 et 9 . i et j variant en fonction de k fixé
    */
    public BooleanFormula RuleFour() { // One number per line
        BooleanFormula[] allSector = new BooleanFormula[9]; // there are 8 slots per line that cannot be the same as the one in input
        int index = 0;

        ArrayList<BooleanFormula> AllRule4 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    for (int l = (i - i%3); l <= (i + 2 - i%3); l++) {
                        for (int m = (j - j%3); m <= (j + 2 - j%3); m++) {
                                allSector[index] = PropVars[l][m][k];
                                index++;
                            }
                        }
                    index = 0;
                    Or or = new Or(allSector[0], allSector[1]);
                    for (int l = 2; l < 9; l++) {
                        or = new Or(or, allSector[l]);
                    }
                    //System.out.println(or);
                    AllRule4.add(or);
                    }

                }
            }
        return new And(AllRule4);
    }

    /*
    Cette fonction retourne les variable qui sont déjà présentent dans la tableau. Le but étant de récupérer que des clauses uniques.
    On reproduit le phénomène de propgatation unitaire
    La formule récupéré est And(L'ensemble des variables déjà assignées)
     */
    public BooleanFormula AlreadyAssigned(char[][] values){
        ArrayList<BooleanFormula> assignedSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!Character.toString(values[i][j]).equals("#")){
                    assignedSlots.add(PropVars[i][j][Character.getNumericValue(values[i][j]-1)]);
                }
            }
        }
        return new And(assignedSlots);

    }






}

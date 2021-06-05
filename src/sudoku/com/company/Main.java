package sudoku.com.company;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import stev.booleans.And;
import stev.booleans.BooleanFormula;
import stev.booleans.Or;

import java.util.*;


public class Main {
    public static ISolver solver;
    static final int MAXVAR = 1000000;
    static final int NBCLAUSES = 500000;
    static ArrayList<String> assignedSlots = new ArrayList<>();


    public static void main(String[] args)
    {
        GameTable t = new GameTable("#26###81#3##7#8##64###5###7#5#1#7#9###39#51###4#3#2#5#1###3###25##2#4##9#38###46#");
        System.out.println(t.caseIsValid(3,3, '4'));
        System.out.println(t);

        Formulas f = new Formulas();
        f.CheckTable(t.getValues());
        //f.AllPossibleValues(t.getValues());
        solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        //t.VarsPropCreation();

        /*for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.println(f.getFormulas().get(9*i + j));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    System.out.println(" var = " + f.getPropVars()[i][j][k]);
                }
            }
        }*/


        for(Object e: f.getFormulas()){
            System.out.println(" Formules : " + e);
        }



        /*
        t.AssignedSlots(assignedSlots);
        for(String e : assignedSlots){
            System.out.println("Already assigned = " + e);
        }

         */

        And BigFormula = new And(f.getFormulas().get(0), f.getFormulas().get(1));
        for (int i = 2; i < f.getFormulas().toArray().length; i++) {
            BigFormula = new And(BigFormula, f.getFormulas().get(i));
        }
        //And BigFormula = new And(f.getAllPossibleValues(), f.getAllImpliesProcessed(),f.getAlreadyAssigned());


        System.out.println(" LE GIGA AND : " + BigFormula);

        BooleanFormula cnf = BooleanFormula.toCnf(BigFormula);

        System.out.println("CNF : " + cnf);

        int[][] clauses = cnf.getClauses();

        int cpt=1;
        for (int[] c: clauses) {
            System.out.println(" Clauses " + cpt + " = " + Arrays.toString(c));
            cpt++;
        }


        for (int i=0; i<clauses.length; i++){
            try {
                solver.addClause(new VecInt(clauses[i]));
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }

        IProblem problem = solver;
        ArrayList<Integer> posNum = new ArrayList<>();
        try {
            if(problem.isSatisfiable()){
                int[] model = problem.model();
                getPositiveNumbers(model, posNum);
                System.out.println(" length : " + posNum.size() + " " + posNum);
            }else{
                System.err.println(" Unsatisfiable");
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        Map<String,Integer> associations = cnf.getVariablesMap();
        String key=null;
        /*for (int i = 0; i <= 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    key = Integer.toString(i+1) + Integer.toString(j+1) + Integer.toString(k+1);
                    System.out.println("Variable " + key + " is associated to number " + associations.get(key) + " et la valeur " + associations.keySet().toArray()[9*i+9*j+9*k]
                    + " et la valeur " + (9*i+9*j+k+1));
                }
            }
        }*/
        for(String keys: associations.keySet()){
            System.out.println(" key = " + keys + " value = " + associations.get(keys));
        }
        System.err.println(" POSNUM size = " + posNum.size());
        for( int n: posNum){
            System.out.println(associations.keySet().toArray()[n]);
            t.set((((String)associations.keySet().toArray()[n]).charAt(0))-'0'-1,
                    ((String)associations.keySet().toArray()[n]).charAt(1)-'0'-1,
                    ((String)associations.keySet().toArray()[n]).charAt(2));
        }
        /*
        for (int i = 0; i < posNum.size(); i++) {
            t.set(Character.getNumericValue(Integer.toString(posNum.get(i)).charAt(0)),
                    Character.getNumericValue(Integer.toString(posNum.get(i)).charAt(1)),
                     Character.forDigit(associations.get(String.valueOf(posNum.get(i))), 10 ));
        }*/

        System.out.println(t);






    }

    public static void getPositiveNumbers(int[] model, ArrayList<Integer> posNum){
        for (int i = 0; i < model.length; i++) {
            if(model[i]>0){
                posNum.add(model[i]);
            }
        }
    }








}

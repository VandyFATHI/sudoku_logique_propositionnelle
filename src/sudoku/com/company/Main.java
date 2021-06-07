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
        System.out.println(t);

        Formulas f = new Formulas();
        solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);


        System.out.println("---------------ALREADY ASSIGNED---------");
        System.out.println(f.AlreadyAssigned(t.getValues()));
        System.out.println("---------------PROPRIETE 1 AT LEAST ONE ---------");
        System.out.println(f.RuleAtLeastOne());
        System.out.println("---------------PROPRIETE 1 AT MOST ONE ---------");
        System.out.println(f.RuleAtMostOne());
        System.out.println("---------------PROPRIETE 2 BIS---------");
        System.out.println(f.RuleTwo());
        System.out.println("---------------PROPRIETE 3 BIS---------");
        System.out.println(f.RuleThree());
        System.out.println("---------------PROPRIETE 4 BIS---------");
        System.out.println(f.RuleFour());


       And BigFormula = new And(
                f.RuleAtLeastOne(),
                f.RuleAtMostOne(),
                f.RuleTwo(),
                f.RuleThree(),
                f.RuleFour(),
               f.AlreadyAssigned(t.getValues()));


        //System.out.println(" BIG FORMULA : " + BigFormula);

        BooleanFormula cnf = BooleanFormula.toCnf(BigFormula);

        //System.out.println(" CNF : " + cnf);

        int[][] clauses = cnf.getClauses();

        /*
        int cpt=1;
        for (int[] c: clauses) {
            System.out.println(" Clauses " + cpt + " = " + Arrays.toString(c));
            cpt++;
        }
        */

        for (int i=0; i<clauses.length; i++){
            try {
                solver.addClause(new VecInt(clauses[i]));
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }

        IProblem problem = solver;
        ArrayList<Integer> posNum = new ArrayList<>(); // Contient que les valeurs positives du model du solver => les solutions
        try {
            if(problem.isSatisfiable()){
                int[] model = problem.model();
                getPositiveNumbers(model, posNum);
            }else{
                System.err.println("----------------------------------");
                System.err.println(" UNSATISFIABLE");
                System.err.println("----------------------------------");
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        Map<String,Integer> associations = cnf.getVariablesMap();
        Set<String> keys =  associations.keySet();
        Collection<Integer> clause_values = associations.values();
        ArrayList<String> result_values = new ArrayList<>();
        int index = 0;
        for (Integer val: posNum) {
            index = 0;
            for (Integer key: clause_values) {
                if (key.equals(val)){
                    result_values.add((String) keys.toArray()[index]);
                }
                index++;
            }
        }

        index=0;
        for (String r: result_values) {
            //solved_table[Character.getNumericValue(r.charAt(0))-1][Character.getNumericValue(r.charAt(1))-1] = r.charAt(2);
            t.set(Character.getNumericValue(r.charAt(0))-1, Character.getNumericValue(r.charAt(1))-1, r.charAt(2));
        }
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

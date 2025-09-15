package SecretSantaJava;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class SecretSantaTester {

    /**
     * This function acts as a helper to testGenerateNewSantaSolution
     * Returns the number of possible Secret Santa solutions for a group of n participants
     * Note: n must be >= 2 for this derangement(a type of mathematical permutation) to work, and for the purposes of Secret Santa n must be >=3
     * @param n the number of participants
     * @return the number of possible Secret Santa solutions given n participants
     */
    public static int possibleSolutions(int n) {
        if (n == 0) {
            return 1;
        }
        if (n == 1) {
            return 0;
        }
        return (n-1)*(possibleSolutions(n-1) + possibleSolutions(n-2));
    }

    /**
     * Tests that the number of entries in the solution map is equal to the number of participants
     *      Starts by testing a participants list of size 3, and goes up to lists of size 9 (inclusive)
     *      Tests each list size 10 times, generating a new Secret Santa solution each time
     */
    @Test
    public void testCorrectSolutionSize() {
        SecretSanta s = new SecretSanta();
        for (int i=0; i < 3; ++i) {//adds participants to the list until there are a total of 3
            s.addParticipant(Integer.toString(i));
        }
        for (int j=3; j < 10; ++j) { //tests solutions sets size 3 - 9
            for (int q=0; q < 10; ++q) { //test each list size 10 times
                s.generateNewSantaSolution();
                int solutionSize = s.getSantaToHuman().size();
                if (solutionSize != j) {
                    assertEquals(j, solutionSize);
                }
            }
            s.addParticipant(Integer.toString(j));
        }
    }

    /**
     * Tests the generateNewSantaSolution() function in SecretSanta.java
     * First, it creates a new SecretSanta object, s, and adds 3 participants to s's participants list
     * Then, it goes into a loop that will test to see if generateNewSantaSolution() generates exactly
     * all of the possible solutions (no more, no less) for the given number of participants.
     * testingManySolutions() tests that the generateNewSantaSolution() does not come up with more solutions than what are possible.
     * In other words, that it does not come up with wrong solutions
     * testingAllSolutions() tests that generateNewSantaSolution() can come up with all of the possible solutions.
     * If both testingManySolutions and testingAllSolutions pass for each number of participants up to (and including) 8 participants,
     * then the test passes.
     * Note: I tested participant groups of size 3-8 because my computer cannot handle testing groups of size >= 9
     */
    @Test
    public void testGenerateNewSantaSolution() {
        boolean test1Passed = false;
        boolean test2Passed = false;
        SecretSanta s = new SecretSanta();
        int numParticipants = 0;

        //adds participants to the list until there are a total of 3
        while (numParticipants < 3) {
            s.addParticipant(Integer.toString(numParticipants));
            numParticipants++;
        }
        //Starting with 3 participants, get the totalPossibleSolutions,
        //then run testingManySolutions() and testingAllSolutions() on groups of size numParticipants
        //Finally, add another participant to the list of participants in stored in s
        while (numParticipants < 9) {
            int totalPossibleSolutions = possibleSolutions(numParticipants);
            test1Passed = testingManySolutions(s, totalPossibleSolutions);
            test2Passed = testingAllSolutions(s, totalPossibleSolutions);

            assertEquals(true, test1Passed);
            assertEquals(true, test2Passed);

            s.addParticipant(Integer.toString(numParticipants));
            numParticipants++;
        }
    }

    /**
     * Tests that the generateNewSantaSolution() does not come up with more solutions than what are possible.
     * In other words, that it does not come up with wrong solutions
     * @param s is the Secret Santa object with the participants list
     * @param totalPossibleSolutions is the total possible number of Secret Santa solutions given a certain number of participants
     * @return false if allSolutions.size() does not equal the totalPossible Solutions after solutions have been generated a significant number of times
     *      or true if allSolutions does contain all of the possible Secret Santa solutions for the given number of participants
     */
    public static boolean testingManySolutions(SecretSanta s, int totalPossibleSolutions) {
        List<Map<String, String>> allSolutions = new LinkedList<Map<String, String>>();
        for(int i=0; i < 20*totalPossibleSolutions; ++i) //probably can't structure it like this, b/c what if solution tally is consistently > than totalPossibleSolutions
        {
            s.generateNewSantaSolution();
            Map<String, String> santaToHuman = s.getSantaToHuman();
            if (!allSolutions.contains(santaToHuman)) {
                Map<String, String> temp = new HashMap<String, String>();
                for (Map.Entry<String, String> pair : santaToHuman.entrySet()) {
                    temp.put(pair.getKey(), pair.getValue());
                }
                allSolutions.add(temp);
            }
        }
        if (allSolutions.size() != totalPossibleSolutions) {
            assertEquals(totalPossibleSolutions, allSolutions.size());
            return false;
        }
        return true;
    }

    /**
     * Tests that generateNewSantaSolution() can come up with all of the possible Secret Santa solutions
     * @param s is the Secret Santa object with the participants list
     * @param totalPossibleSolutions is the total possible number of Secret Santa solutions given a certain number of participants
     * @return true if allSolutions does contain all of the possible Secret Santa solutions for the given number of participants
     */
    public static boolean testingAllSolutions(SecretSanta s, int totalPossibleSolutions) {
        List<Map<String, String>> allSolutions = new LinkedList<Map<String, String>>();
        while(allSolutions.size() < totalPossibleSolutions) {
            s.generateNewSantaSolution();
            Map<String, String> santaToHuman = s.getSantaToHuman();
            if (!allSolutions.contains(santaToHuman)) {
                Map<String, String> temp = new HashMap<String, String>();
                for (Map.Entry<String, String> pair : santaToHuman.entrySet()) {
                    temp.put(pair.getKey(), pair.getValue());
                }
                allSolutions.add(temp);
            }
        }
        return true;
    }


    /**
     * This method tests that the generateParticipantsList function in SecretSanta.java works properly
     * To "work properly" it must create a comma separated String of all the participants in the list participants.
     */
    @Test
    public void testGenerateParticipantsList()
    {
        SecretSanta s = new SecretSanta();
        s.addParticipant("Megan");
        s.addParticipant("Faith");
        s.addParticipant("Julio");
        s.addParticipant("Kate");

        String testOutput = s.generateParticipantsList();
        if (!testOutput.equals("Megan, Faith, Julio, Kate")) {
            printError("Error when printing all participants names.","Megan, Faith, Julio, Kate", testOutput);
        }
        assertEquals("Megan, Faith, Julio, Kate", testOutput);
    }


    /**
     * Prints an error message when a comparison between integers fails
     * @param msg - the error message
     * @param expected - expected int value
     * @param actual - actual int value
     */
    private void printError(String msg, Object expected, Object actual)
    {
        System.out.println(msg);
        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + actual);
    }
}


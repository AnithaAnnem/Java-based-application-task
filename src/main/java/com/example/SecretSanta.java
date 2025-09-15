package SecretSantaJava;

import javax.swing.*;
import java.util.*;
import java.util.Scanner;

public class SecretSanta {

    private List<String> participants;
    private Map<String, String> santaToHuman;
    private Map<String, String> humanToSanta;

    public SecretSanta() {
        participants = new ArrayList<String>();
        santaToHuman = new HashMap<String, String>();
        humanToSanta = new HashMap<String, String>();
    }

    /**
     * Adds a name to the participants instance variable
     * @param name the new participant
     */
    public void addParticipant(String name) { participants.add(name); }

    /**
     * Used in SecretSantaTester to test out the generated secret santa solution
     * @return the santaToHuman instance variable of the Santa object
     */
    public Map<String, String> getSantaToHuman() { return santaToHuman; }

    /**
     * Randomly generate a santa-human pairs for the participants given.
     * Save the pairs to the santaToHuman map and humanToSanta map.
     *
     * Note: My solution allows for solutions such as
     * Participants = [a, b, c, d]
     * SantaToHuman = {a=b, b=a, c=d, d=c}
     * This solution is comprised of two subgroups where the first person is the Santa of the second and the second is the Santa first.
     * I allow this to happen because it closer to a truly random solution.
     * However, it can cause a problem because the last person cannot have themselves.
     * I solve for this with the remainingHumans list:
     *     If there are two remaining humans and one of the remaining is the last Santa, that Santa cannot have themselves,
     *     so the second-to-last Santa must have the last Santa as their human
     */
    private void generateSantaSolution() {
        List<String> remainingHumans = new ArrayList<String>();
        //deep copy of participants to remainingHumans
        for (String santa : participants) {
            remainingHumans.add(santa);
        }
        String lastSanta = participants.get(participants.size() - 1);
        for (String santa : participants) {
            String human = "";
            if (remainingHumans.size() == 1) {
                human = remainingHumans.get(0);
            }
            //if there are two remaining humans and oen of the remaining is the last Santa, that Santa cannot have themselves,
            //so the second-to-last Santa must have the last Santa as their human
            else if (remainingHumans.size() == 2 && remainingHumans.contains(lastSanta)) {
                human = lastSanta;
                remainingHumans.remove(lastSanta);
            }
            else {
                while (true) { //randomly select next human from the remaining Santas
                    int randomIndex = (int) (Math.random() * remainingHumans.size());
                    human = remainingHumans.get(randomIndex);
                    if (!human.equals(santa) && !santaToHuman.containsValue(human)) {
                        remainingHumans.remove(human);
                        break;
                    }
                }
            }
            santaToHuman.put(santa, human);
            humanToSanta.put(human, santa);
        }
    }


    /**
     * Erase the existing solution by clearing the santaToHuman and humanToSanta maps.
     * Then, generate a new santa solution by calling generateSantaSolution().
     */
    public void generateNewSantaSolution() {
        santaToHuman.clear();
        humanToSanta.clear();
        generateSantaSolution();
    }

    /**
     * Create a comma separated String of all the participants in participants.
     * @return the String of all participants
     */
    public String generateParticipantsList() {
        String allParticipants = "";
        for (String participant : participants) {
            if (participants.indexOf(participant) != 0) {
                allParticipants += ", " + participant;
            }
            else {
                allParticipants += participant;
            }
        }
        return allParticipants;
    }

    /**
     * Print the usage for the game mode.
     * The usage is all accepted inputs from the user and a brief explanation of that input's functionality.
     */
    public void printGameUsage() {
        System.out.print("\nParticipants: " + generateParticipantsList() + "\n");
        System.out.print("Regular Usage:\n");
        System.out.print("\t Type in your name, Santa, to get your Human.\n");
        System.out.print("Other Usage:\n");
        System.out.print("\t \"Help\" to print usage\n");
        System.out.print("\t \"Edit\" to edit the particpants list and generate a new solution\n");
        System.out.print("\t \"Get my santa\" to enter a Human and get their Santa\n");
        System.out.print("\t \"Generate new solution\" to generate a new set of Santa and Human pairs.\n");
        System.out.println("\t \"End\" to end the program");
    }

    /**
     * Print the usage for the setup mode
     * The usage is all accepted inputs from the user and a brief explanation of that input's functionality.
     */
    public void printSetupUsage() {
        System.out.print("Usage:\n");
        System.out.print("\t \"Help\" to print usage\n");
        System.out.print("\t \"List\" to get current participant list\n");
        System.out.print("\t \"Remove {name}\" to remove that name from the list\n");
        System.out.print("\t \"Done\" when participants list is complete\n");
        System.out.println("\t \"End\" to end the program");
    }

    /**
     * Capitalize the first letter only of the name.
     * Make all other letter lowercase.
     * @param name The name to be capitalized
     * @return The capitalized name
     */
    public String capitalizeFirstLetterOnly(String name) {
        return Character.toString(name.charAt(0)).toUpperCase() + name.toLowerCase().substring(1);
    }

    /**
     * Check to see if name is already in the list of participants
     * @param name the name to be checked
     * @return true if the name is already in the list, else return false
     */
    public boolean isDuplicateName(String name) {
        for (var i = 0; i < participants.size(); i++) {
            if (participants.get(i).equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Allow user to setup the game by creating a list or participants
     * Then, allow user to play the game to see what Human each Santa has
     * @param args
     */
    public static void main(String[] args) {

        //Setup
        SecretSanta s = new SecretSanta();

        Scanner in = new Scanner(System.in);
        s.printSetupUsage();

        while(true) {
            System.out.println("Type: a participants name, \"done\" to play, or \"help\" to get usage.");
            String name = in.nextLine();
            name = s.capitalizeFirstLetterOnly(name);
            if (s.isDuplicateName(name)) {
                System.out.println("The name \"" + name + "\" is already in your participants list. Please type a unique name.");
            }
            else if (name.equals("Help")) {
                s.printSetupUsage();
            }
            else if (name.equals("List")) {
                if (s.participants.size() > 0) {
                    System.out.println(s.generateParticipantsList());
                }
                else {
                    System.out.println("There are currently no participants in the list.");
                }
            }
            else if (name.equals("Remove") || name.equals("Remove ")) {
                System.out.println("Please type a name to remove. Usage: \"Remove {name}\"");
            }
            else if (name.length() > "Remove".length()+1 && name.substring(0, "Remove".length()).equals("Remove")) {
                String nameToBeRemoved = s.capitalizeFirstLetterOnly(name.substring("Remove".length() + 1, name.length()));
                if (s.participants.contains(nameToBeRemoved)) {
                    s.participants.remove(nameToBeRemoved);
                    System.out.print(nameToBeRemoved + " was removed from the participants list.\n");
                    if (s.participants.size() > 0) {
                        System.out.println("Current participants: " + s.generateParticipantsList());
                    }
                    else {
                        System.out.println("There are currently no participants in the list.");
                    }
                }
                else {
                    System.out.println(nameToBeRemoved + " is not in the participants list: " + s.generateParticipantsList());
                }
            }
            else if (name.equals("Done")) {
                if (s.participants.size() < 3) {
                    if (s.participants.size() == 1) {
                        System.out.print("There is only " + s.participants.size() + " participant in your group.\n");
                    }
                    else {
                        System.out.print("There are only " + s.participants.size() + " participants in your group.\n");
                    }
                    System.out.println("Please have at least 3 participants.");
                }
                else {
                    break;
                }
            }
            else if (name.equals("End")) {
                return;
            }
            else {
                s.addParticipant(name);
            }
            System.out.println();
        }

        String allParticipants = s.generateParticipantsList();

        s.generateNewSantaSolution();

        //Game Play
        s.printGameUsage();

        JFrame frame = new JFrame();

        while(true) {
            System.out.println("\nParticipants: " + allParticipants);
            System.out.println("What is your name, Santa? ");
            String santa = s.capitalizeFirstLetterOnly(in.nextLine());
            if (s.santaToHuman.containsKey(santa)) {
                JOptionPane.showMessageDialog(frame, "Your human is " + s.santaToHuman.get(santa));
            }
            else if (santa.equals("Help")) {
                s.printGameUsage();
            }
            else if (santa.equals("Edit")) {
                main(args);
                return;
            }
            else if (santa.equals("Get my santa")) {
                System.out.println("For which Human would you like to know the Santa?");
                String whichHuman = s.capitalizeFirstLetterOnly(in.nextLine());
                JOptionPane.showMessageDialog(frame, "Santa: " + s.humanToSanta.get(whichHuman) + ". Human: " + whichHuman);
            }
            else if (santa.equals("Generate new solution")) {
                s.generateNewSantaSolution();
                System.out.println("New solution generated.");
            }
            else if (santa.equals("End")) {
                System.out.println("\nThanks for playing!");
                return;
            }
            else {
                System.out.println(santa + " is not a participant. Please enter one of the participant names: " + allParticipants);
            }
        }
    }
}
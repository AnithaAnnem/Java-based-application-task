package SecretSantaJava;

import java.util.*;
import java.util.Scanner;

public class SecretSanta {

    private List<String> participants;
    private Map<String, String> santaToHuman;
    private Map<String, String> humanToSanta;

    public SecretSanta() {
        participants = new ArrayList<>();
        santaToHuman = new HashMap<>();
        humanToSanta = new HashMap<>();
    }

    public void addParticipant(String name) { 
        participants.add(name); 
    }

    public Map<String, String> getSantaToHuman() { 
        return santaToHuman; 
    }

    private void generateSantaSolution() {
        List<String> remainingHumans = new ArrayList<>(participants);
        String lastSanta = participants.get(participants.size() - 1);

        for (String santa : participants) {
            String human = "";

            if (remainingHumans.size() == 1) {
                human = remainingHumans.get(0);
            } else if (remainingHumans.size() == 2 && remainingHumans.contains(lastSanta)) {
                human = lastSanta;
                remainingHumans.remove(lastSanta);
            } else {
                while (true) {
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

    public void generateNewSantaSolution() {
        santaToHuman.clear();
        humanToSanta.clear();
        generateSantaSolution();
    }

    public String generateParticipantsList() {
        return String.join(", ", participants);
    }

    public void printGameUsage() {
        System.out.println("\nParticipants: " + generateParticipantsList());
        System.out.println("Regular Usage:");
        System.out.println("\tType in your name, Santa, to get your Human.");
        System.out.println("Other Usage:");
        System.out.println("\t\"Help\" to print usage");
        System.out.println("\t\"Edit\" to edit the participants list and generate a new solution");
        System.out.println("\t\"Get my santa\" to enter a Human and get their Santa");
        System.out.println("\t\"Generate new solution\" to generate a new set of Santa and Human pairs.");
        System.out.println("\t\"End\" to end the program");
    }

    public void printSetupUsage() {
        System.out.println("Usage:");
        System.out.println("\t\"Help\" to print usage");
        System.out.println("\t\"List\" to get current participant list");
        System.out.println("\t\"Remove {name}\" to remove that name from the list");
        System.out.println("\t\"Done\" when participants list is complete");
        System.out.println("\t\"End\" to end the program");
    }

    public String capitalizeFirstLetterOnly(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.toLowerCase().substring(1);
    }

    public boolean isDuplicateName(String name) {
        return participants.contains(name);
    }

    public static void main(String[] args) {
        SecretSanta s = new SecretSanta();
        Scanner in = new Scanner(System.in);

        // Setup
        s.printSetupUsage();

        while (true) {
            System.out.println("\nType: a participant's name, \"done\" to play, or \"help\" to get usage.");
            String name = in.nextLine().trim();
            name = s.capitalizeFirstLetterOnly(name);

            if (s.isDuplicateName(name)) {
                System.out.println("The name \"" + name + "\" is already in your participants list. Please type a unique name.");
            } else if (name.equalsIgnoreCase("Help")) {
                s.printSetupUsage();
            } else if (name.equalsIgnoreCase("List")) {
                if (!s.participants.isEmpty()) {
                    System.out.println(s.generateParticipantsList());
                } else {
                    System.out.println("There are currently no participants in the list.");
                }
            } else if (name.equalsIgnoreCase("Remove") || name.equalsIgnoreCase("Remove ")) {
                System.out.println("Please type a name to remove. Usage: \"Remove {name}\"");
            } else if (name.toLowerCase().startsWith("remove ")) {
                String nameToRemove = s.capitalizeFirstLetterOnly(name.substring("Remove ".length()));
                if (s.participants.contains(nameToRemove)) {
                    s.participants.remove(nameToRemove);
                    System.out.println(nameToRemove + " was removed from the participants list.");
                    if (!s.participants.isEmpty()) {
                        System.out.println("Current participants: " + s.generateParticipantsList());
                    } else {
                        System.out.println("There are currently no participants in the list.");
                    }
                } else {
                    System.out.println(nameToRemove + " is not in the participants list: " + s.generateParticipantsList());
                }
            } else if (name.equalsIgnoreCase("Done")) {
                if (s.participants.size() < 3) {
                    System.out.println("Please have at least 3 participants. Current: " + s.participants.size());
                } else {
                    break;
                }
            } else if (name.equalsIgnoreCase("End")) {
                return;
            } else {
                s.addParticipant(name);
            }
        }

        String allParticipants = s.generateParticipantsList();
        s.generateNewSantaSolution();
        s.printGameUsage();

        // Game Play
        while (true) {
            System.out.println("\nParticipants: " + allParticipants);
            System.out.println("What is your name, Santa?");
            String santa = s.capitalizeFirstLetterOnly(in.nextLine().trim());

            if (s.santaToHuman.containsKey(santa)) {
                System.out.println("Your human is " + s.santaToHuman.get(santa));
            } else if (santa.equalsIgnoreCase("Help")) {
                s.printGameUsage();
            } else if (santa.equalsIgnoreCase("Edit")) {
                main(args); // restart setup
                return;
            } else if (santa.equalsIgnoreCase("Get my santa")) {
                System.out.println("For which Human would you like to know the Santa?");
                String whichHuman = s.capitalizeFirstLetterOnly(in.nextLine().trim());
                System.out.println("Santa: " + s.humanToSanta.get(whichHuman) + ". Human: " + whichHuman);
            } else if (santa.equalsIgnoreCase("Generate new solution")) {
                s.generateNewSantaSolution();
                System.out.println("New solution generated.");
            } else if (santa.equalsIgnoreCase("End")) {
                System.out.println("\nThanks for playing!");
                return;
            } else {
                System.out.println(santa + " is not a participant. Please enter one of the participant names: " + allParticipants);
            }
        }
    }
}

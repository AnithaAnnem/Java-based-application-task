package SecretSantaJava;

import java.util.*;

public class SecretSanta {

    private final List<String> participants = new ArrayList<>();
    private final Map<String, String> santaToHuman = new HashMap<>();

    // Add participant
    public void addParticipant(String name) {
        participants.add(name);
    }

    // Generate Secret Santa assignments
    private void generateSantaSolution() {
        List<String> remainingHumans = new ArrayList<>(participants);
        Random rand = new Random();

        for (String santa : participants) {
            String human;
            do {
                human = remainingHumans.get(rand.nextInt(remainingHumans.size()));
            } while (human.equals(santa)); // avoid assigning self
            santaToHuman.put(santa, human);
            remainingHumans.remove(human);
        }
    }

    // Print assignments
    private void printAssignments() {
        System.out.println("\nSecret Santa Assignments:");
        santaToHuman.forEach((santa, human) -> System.out.println(santa + " -> " + human));
    }

    public static void main(String[] args) throws InterruptedException {
        SecretSanta ss = new SecretSanta();

        // 1️⃣ Read participants from environment variable
        String participantsEnv = System.getenv("PARTICIPANTS");
        if (participantsEnv == null || participantsEnv.isEmpty()) {
            System.err.println("No participants specified! Set PARTICIPANTS environment variable, e.g., Alice,Bob,Charlie");
            System.exit(1);
        }

        for (String name : participantsEnv.split(",")) {
            ss.addParticipant(name.trim());
        }

        // 2️⃣ Generate Secret Santa assignments
        ss.generateSantaSolution();
        ss.printAssignments();

        // 3️⃣ Keep the pod alive
        System.out.println("\nPod is now running. Press Ctrl+C to terminate...");
        while (true) {
            Thread.sleep(60000); // sleep 1 minute
        }
    }
}

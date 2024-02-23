import java.util.Random;
import java.util.concurrent.Semaphore;

// class representing the labyrinth
class Labyrinth {

  private Semaphore mutex;
  private Semaphore minotaurSemaphore;
  private boolean cupcakeAvailable;

  // Labyrinth constructor
  public Labyrinth() {
    this.mutex = new Semaphore(1);
    this.minotaurSemaphore = new Semaphore(1);
    this.cupcakeAvailable = true;
  }

  // Method for cupcake request as well as
  // a method that deals with eating decision
  public boolean requestCupcake() throws InterruptedException {
    Random random = new Random();
    if (cupcakeAvailable) {
      return false;
    }
    System.out.println("Guest requests another cupcake.");
    System.out.println("The servants bring a new cupcake.");
    cupcakeAvailable = true;
    boolean decision = random.nextInt(2) == 1;
    return decision;
  }

  // Method that deals with the guest labyrinth logic
  public void enterLabyrinth(String guestName) throws InterruptedException {
    System.out.println(guestName + " enters the labyrinth.");
    minotaurSemaphore.acquire();
    mutex.acquire();
    if (cupcakeAvailable) {
      System.out.println(guestName + " found the cupcake.");
      System.out.println(guestName + " ate the cupcake.");
      cupcakeAvailable = false;
    } else {
      boolean cupcakeRequested = requestCupcake();
      if (cupcakeRequested) {
        System.out.println(guestName + " decides to eat the new cupcake.");
        cupcakeAvailable = false;
      } else {
        System.out.println(guestName + " decides not to eat the new cupcake.");
      }
    }
    mutex.release();
    minotaurSemaphore.release();
  }
}

class Guest extends Thread {

  private String name;
  private Labyrinth labyrinth;

  // Thread constructor
  public Guest(String name, Labyrinth labyrinth) {
    this.name = name;
    this.labyrinth = labyrinth;
  }

  // Overriden run method
  @Override
  public void run() {
    try {
      labyrinth.enterLabyrinth(name);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

public class Problem1 {

  // Helper method to ensure everyone has entered the labyrinth
  private static boolean allEntered(boolean[] hasEntered) {
    for (boolean entered : hasEntered) {
      if (!entered) return false;
    }
    return true;
  }

  public static void main(String[] args) {
    int totalGuests = 5;
    Labyrinth labyrinth = new Labyrinth();
    Random random = new Random();

    // Loop that randomly selects a thread (Guest) and starts it.
    boolean[] hasEntered = new boolean[totalGuests];
    while (!allEntered(hasEntered)) {
      int guestIndex = random.nextInt(totalGuests);
      Guest guest = new Guest("Guest " + (guestIndex + 1), labyrinth);
      guest.start();
      try {
        guest.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      hasEntered[guestIndex] = true;
    }

    System.out.println("All guests have visited and exited the labyrinth.");
  }
}

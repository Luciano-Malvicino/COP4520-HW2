import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Showroom {

  private boolean available;
  private Queue<String> queue;

  public Showroom() {
    this.available = true;
    this.queue = new LinkedList<>();
  }

  public synchronized void enter(String guestName) throws InterruptedException {
    if (!available) {
      queue.add(guestName);
      wait();
    }
    System.out.println(guestName + " enters the showroom.");
    available = false;
  }

  public synchronized void exit(String guestName) {
    System.out.println(guestName + " exits the showroom.");
    if (!queue.isEmpty()) {
      String nextGuest = queue.poll();
      System.out.println("Notifying " + nextGuest + " that the showroom is available.");
      // Notify the next guest in the queue
      notify(); 
    } else {
      available = true;
    }
  }
}

class Guest extends Thread {

  private String name;
  private Showroom showroom;

  public Guest(String name, Showroom showroom) {
    this.name = name;
    this.showroom = showroom;
  }

  @Override
  public void run() {
    try {
      showroom.enter(name);
      // simulating a guest viewing the vase
      Thread.sleep(2000);
      showroom.exit(name);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

public class Problem2 {

  public static void main(String[] args) {
    int totalGuests = 5;
    Showroom showroom = new Showroom();

    List<Guest> guests = new ArrayList<>();

    // Create guests and adding them to a list
    for (int i = 0; i < totalGuests; i++) {
      guests.add(new Guest("Guest " + (i + 1), showroom));
    }

    // Shuffle the order of guests so that
    // guests enter the queue in a random order
    Collections.shuffle(guests);

    // Starting the guests in the random order
    for (Guest guest : guests) {
      guest.start();
    }
  }
}

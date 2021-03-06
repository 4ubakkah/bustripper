package io.telenor.bustripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final String SEARCH_TERM_ALLOWED_SYMBOLS_REGEXP = "[^a-zA-Z ]";

        /**
         * Waits for bustrip results and print the 10 first results sorted by expected arrival time.
        **/
        private static class BustripWaiter implements TripsCallback {

            private boolean done = false;
            private Set<BusTrip> allTrips = new HashSet<>();
            private static int maxtrips = 10;

            @Override
            public synchronized void gotTrips(Set<BusTrip> trips, boolean done) {
                allTrips.addAll(trips);

                if(done || allTrips.size() >= maxtrips) {
                    if (allTrips.isEmpty()) {
                        System.out.println("No trips found!");
                    }

                    trips.stream().sorted(
                            (e1, e2) -> e1.getExpectedArrivalTime().compareTo(e2.getExpectedArrivalTime())
                    ).limit(maxtrips).forEach(t -> System.out.println(t));

                    this.done = true;
                    notify();
                }
            }

            @Override
            public synchronized void failedGettingTrips(IOException io) {
                System.out.println("Failed getting trips. " + io.getMessage());
                this.done = true;
                notify();
            }

            public synchronized boolean waitForCompletion() throws InterruptedException{
                while(!done) {
                    wait();
                }
                return done;
            }
        }

        private static class InputGatherer implements Runnable {

            private static final String EXIT_KEY = "q";

            public void run() {
                System.out.println("Welcome to the bus line lookup tool. Add a search phrase to look up a bus stop.");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                boolean done = false;

                do {
                    System.out.print("> ");
                    try {
                        //#1: Normalize search term - remove all symbols except alphabetic and space
                        String searchterm = normalizeSearchTerm(in.readLine());
                        //#2: Compare strings using equals instead of comparing by reference
                        if(EXIT_KEY.equals(searchterm) || searchterm.length() == 0) {
                            System.exit(0);
                        }
                        System.out.println("Looking up " + searchterm);
                        BustripWaiter waiter = new BustripWaiter();
                        new Thread(new FindBusStop(waiter, searchterm)).start();
                        waiter.waitForCompletion();
                    }
                    catch (IOException | InterruptedException ex) {
                        done = true;
                    }

                } while (!done);

            }
        }

        private static String normalizeSearchTerm(String input) {
            return input.replaceAll(SEARCH_TERM_ALLOWED_SYMBOLS_REGEXP, "");
        }


        public static void main(String[] args) {
            new Thread(new InputGatherer()).start();
        }
    }

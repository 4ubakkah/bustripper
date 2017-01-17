package io.telenor.bustripper;

import java.io.IOException;
import java.util.Set;

/**
 * Callback class to client app requesting some bustrips.
 */
public interface TripsCallback {

    /**
     * Got a list of trips
     * @param trips the set of bus trips found
     * @param done true if this call is the last batch of trips and no more callbacks will be given.
     */
    void gotTrips(Set<BusTrip> trips, boolean done);

    /**
     * Failed getting the list of trips.
     * @param io trouble found
     */
    void failedGettingTrips(IOException io);
}

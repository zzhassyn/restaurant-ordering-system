package com.restaurant.observer;

// Subject: keeps a list of observers and notifies them about its changes
public interface OrderSubject {

    void addObserver(OrderObserver observer);

    void removeObserver(OrderObserver observer);

    void notifyObservers();
}

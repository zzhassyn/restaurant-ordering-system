package com.restaurant.observer;

import com.restaurant.order.Order;

// Observer: is notified every time an order it is subscribed to changes its status
public interface OrderObserver {

    void update(Order order);
}

package com.restaurant.observer;

import com.restaurant.order.Order;


public interface OrderObserver {

    void update(Order order);
}

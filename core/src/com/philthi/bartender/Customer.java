package com.philthi.bartender;

import java.util.ArrayList;

/**
 * Created by phil on 15/10/17.
 */

public class Customer implements Touchable {
    Order order;
    int x;
    int y;

    public Customer(Order order, int x, int y){
        this.order = order;
        this.x = x;
        this.y = y;
    }

    public boolean Activate(Order order, ArrayList<Customer> waitingCustomers) {
        if(order.equals(this.order)){
            order.reset();
            waitingCustomers.remove(this);
            return true;
        }

        return false;
    }
}

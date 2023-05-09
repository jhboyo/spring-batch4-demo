package com.derek.batch4.core.domain.accounts;

import com.derek.batch4.core.domain.orders.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String orderItem;
    private String price;
    private Date orderDate;
    private Date accountDate;

    public Accounts(Orders orders) {
        Id = orders.getId();
        this.orderItem = orders.getOrderItem();
        this.price = orders.getPrice();
        this.orderDate = orders.getOrderDate();
        this.accountDate = new Date();
    }
}

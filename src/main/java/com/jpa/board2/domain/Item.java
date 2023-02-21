package com.jpa.board2.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "ITEM")
public class Item {

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private long itemId;

    private int price;
    private int stockQuantity;
}

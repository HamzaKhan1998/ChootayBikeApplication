package com.example.ChootayBikeApp.shoppingCart;

import com.example.ChootayBikeApp.appUser.AppUser;
import com.example.ChootayBikeApp.cartItem.CartItem;
import com.example.ChootayBikeApp.item.Item;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity(name = "ShoppingCart")
@Table
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private float price;

    private Date date;

    private boolean completed = false;

    @ManyToOne
    @JoinColumn//(name = "user_id")
    private AppUser user;

    @OneToMany
    private List<CartItem> cartItems = new ArrayList<>();

    public ShoppingCart() {
    }

    public ShoppingCart(float price, AppUser user, List<CartItem> cartItems) {
        this.price = price;
        this.user = user;
        this.cartItems = cartItems;
    }

    public ShoppingCart(float price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isItemInCart(Item item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getId() == item.getId())
                return true;
        }
        return false;
    }

    public CartItem getCartItemByItem(Item item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem() == item)
                return cartItem;
        }
        return null;
    }

    public float calcCartPrice() {
        float price = 0;

        if (cartItems.isEmpty())
            return price;

        for (CartItem cartItem : cartItems)
            price += (cartItem.getItem().getPrice() * cartItem.getQuantity());

        return price;
    }

    public int calcNumberOfItems() {
        int quantity = 0;

        if (cartItems.isEmpty())
            return quantity;

        for (CartItem cartItem : cartItems)
            quantity += cartItem.getQuantity();

        return quantity;
    }

}

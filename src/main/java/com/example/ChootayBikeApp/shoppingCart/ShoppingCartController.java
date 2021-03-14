package com.example.ChootayBikeApp.shoppingCart;

import com.example.ChootayBikeApp.appUser.AppUser;
import com.example.ChootayBikeApp.appUser.AppUserService;
import com.example.ChootayBikeApp.cartItem.CartItem;
import com.example.ChootayBikeApp.item.Item;
import com.example.ChootayBikeApp.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;
    private AppUserService appUserService;
    private ItemService itemService;


    @Autowired
    public void setUserService(AppUserService userService) {
        this.appUserService = userService;
    }

    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Autowired
    public void setShoppingCartService(ShoppingCartService shoppingCartService) { this.shoppingCartService = shoppingCartService; }

    @GetMapping("shoppingCart")
    public String shoppingCart(Principal principal, Model model){
        AppUser appUser = appUserService.getUser(principal.getName());
        ShoppingCart oldCart = shoppingCartService.getActiveShoppingCartByFirstName(principal.getName(),false);
        if(oldCart==null){
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUser(appUser);
            shoppingCartService.saveShoppingCart(newCart);
            appUser.setShoppingCart(newCart);
            appUserService.saveUser(appUser);
            model.addAttribute("cart", newCart);
        }
        else
            model.addAttribute("cart", oldCart );

        return "shoppingCart";
    }

    @GetMapping("item/{id}")
    public String viewItem(@PathVariable(value = "id") Long id, Model model){
        Item item = itemService.getItemById(id);
        List<Item> result = itemService.getAllItemsByCategory(item.getCategory(), id);
        model.addAttribute("item", item);
        model.addAttribute("items", result );
        System.out.println("Catlist size: "+result.size());

        return "item/item";
    }

    @GetMapping("item/addToCart/{id}")
    public String addToCart(@PathVariable(value="id") Long id, Principal principal){

        if(principal==null)
            return "login";

        ShoppingCart shoppingCart = shoppingCartService.getActiveShoppingCartByFirstName(principal.getName(),false);
        Item itemToAdd = itemService.getItemById(id);

        if(shoppingCart.isItemInCart(itemToAdd)){
            CartItem cartItem = shoppingCart.getCartItemByItem(itemToAdd);
            int cartItemQuantity = cartItem.getQuantity()+1;
            cartItem.setQuantity(cartItemQuantity);
            itemService.saveCartItem(cartItem);
        }
        else{
            CartItem newCartItem = new CartItem(itemToAdd,1);
            newCartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(newCartItem);
            itemService.saveCartItem(newCartItem);
            shoppingCartService.saveShoppingCart(shoppingCart);
        }

        return "redirect:/";
    }

    @GetMapping("addQuantity/{id}")
    public String addQuantity(@PathVariable(value = "id")Long id){
        CartItem cartItem = itemService.getCartItem(id);
        int oldQuantity = cartItem.getQuantity();
        cartItem.setQuantity(oldQuantity + 1);
        itemService.saveCartItem(cartItem);
        shoppingCartService.saveShoppingCart(cartItem.getShoppingCart());

        return "redirect:/shoppingCart";
    }
    @GetMapping("decreaseQuantity/{id}")
    public String decreaseQuantity( @PathVariable(value = "id")Long id, Principal principal ){
        CartItem cartItem = itemService.getCartItem(id);
        int oldQuantity = cartItem.getQuantity();
        cartItem.setQuantity(oldQuantity-1);

        if(oldQuantity<=1){
            ShoppingCart shoppingCart = shoppingCartService.getActiveShoppingCartByFirstName(principal.getName(),false);
            shoppingCart.getCartItems().remove(cartItem);
            shoppingCartService.saveShoppingCart(shoppingCart);
        }
        itemService.saveCartItem(cartItem);
        shoppingCartService.saveShoppingCart(cartItem.getShoppingCart());

        return "redirect:/shoppingCart";
    }

    @GetMapping("order/{id}")
    public String checkOut(@PathVariable(value = "id")Long id, Principal principal, RedirectAttributes attributes){
        AppUser appUser = appUserService.getUser(principal.getName());
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartById(id);
        shoppingCart.setCompleted(true);
        shoppingCart.setPrice(shoppingCart.calcCartPrice());
        shoppingCart.setDate(new Date());
        shoppingCartService.saveShoppingCart(shoppingCart);
        appUser.getShoppingCarts().add(shoppingCart);
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(appUser);
        appUser.setShoppingCart(newCart);
        appUserService.saveUser(appUser);
        attributes.addFlashAttribute("checkoutSuccess", shoppingCart.calcCartPrice());
        attributes.addFlashAttribute("checkoutSuccess2", shoppingCart.calcNumberOfItems());

        return "redirect:/";
    }

}


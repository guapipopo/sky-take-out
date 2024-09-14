package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
//        先判断购物车里是否有相同菜品或套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
//        有，则数量加1
//        没有，则创建
        if(list != null && list.size() > 0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        }else{
//            判断是套餐还是菜品
            if(shoppingCartDTO.getDishId() != null){
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());

            }else{
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);

        }


    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart
                .builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart
                .builder()
                .userId(userId)
                .build();
        shoppingCartMapper.deleteByUserId(shoppingCart);
    }
}

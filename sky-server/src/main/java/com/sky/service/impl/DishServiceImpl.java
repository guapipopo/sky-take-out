package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
//        向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
//        获取insert生成的主键值
        Long id = dish.getId();

//        向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(id));
        if(flavors!=null&&flavors.size()>0){
            dishFlavorMapper.insertBatch(flavors);
        }


    }
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
//        开启分页器
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
//        判断菜品是否起售
        if(dishMapper.getStatusByIds(ids)>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        };
//        判断菜品是否关联套餐
        if(setMealDishMapper.countByDishId(ids)>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        删除菜品及对应口味 判断口味不能为空

        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishId(ids);

    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
//        根据id查询菜品
        Dish dish = dishMapper.getById(id);
//        根据菜品查口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
//        封装成dishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
//修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
//删除原有口味
        List<Long> id = new ArrayList<>();
        id.add(dishDTO.getId());
        dishFlavorMapper.deleteByDishId(id);
//修改口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
//        flavor为空sql会异常
        if(flavors!=null&&flavors.size()>0){
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
    }




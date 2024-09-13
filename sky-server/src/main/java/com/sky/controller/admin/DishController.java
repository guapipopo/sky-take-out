package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags ="菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(DishDTO dishDTO){
        log.info("新增菜品，菜品信息：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
//        商家新增菜品后 把对应分类的缓存数据删除
        String key="dish_"+dishDTO.getCategoryId();
        cleanCacheData(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping ("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public  Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品，id为{}",ids);
        dishService.deleteBatch(ids);
//        缓存里所有的菜品数据 删除
        cleanCacheData("dish_*");
        return Result.success();
    }

    /**
     *  根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        return Result.success(dishService.getByIdWithFlavor(id));
    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息：{}",dishDTO);
        dishService.update(dishDTO);
        cleanCacheData("dish_*");
        return Result.success();
    }

    /**
     *  菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售")
    public Result startOrStop(@PathVariable Integer status,Long id ){
        log.info("菜品起售：{}",id);
        dishService.startOrStop(status,id);
        cleanCacheData("dish_*");
        return Result.success();
    }

    private void cleanCacheData(String key){
        Set keys=redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }

}

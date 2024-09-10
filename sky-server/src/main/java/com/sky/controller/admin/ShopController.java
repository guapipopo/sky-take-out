package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;
    @ApiOperation("修改营业状态")
    @PutMapping("/{status}")
    public Result changeStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set("status",status);
        log.info("修改营业状态为：{}",status==0? "打烊中" : "营业中");
        return Result.success();
    }

    @ApiOperation("查询营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get("status");
        log.info("修改营业状态为：{}",status==0? "打烊中" : "营业中");
        return Result.success(status);
    }

}

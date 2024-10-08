package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
    /**
     *  历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(Integer page, Integer pageSize,Integer status);
    /**
     *  订单详情查询
     * @param id
     * @return
     */
    OrderVO details(Long id);
    /**
     *  取消订单
     * @param id
     * @return
     */
    void cancel(Long id) throws Exception;
    /**
     * 再来一单
     * @param id
     * @return
     */
    void repetition(Long id);
    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     *  订单状态数量统计
     * @return
     */
    OrderStatisticsVO statistics();
    /**
     *  接单
     * @param ordersConfirmDTO
     * @return
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);
    /**
     *  拒单
     * @param ordersConfirmDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersConfirmDTO) throws Exception;
    /**
     * 取消订单
     *
     * @return
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);
    /**
     * 派送订单
     *
     * @return
     */
    void delivery(Long id);
    /**
     * 完成订单
     *
     * @return
     */
    void complete(Long id);
}

package com.sky.mapper;

import com.sky.entity.OrderDetail;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情数据
     * @param orderDetailList 订单详情列表
     */
    void insertBatch(@Param("orderDetailList") List<OrderDetail> orderDetailList);
}

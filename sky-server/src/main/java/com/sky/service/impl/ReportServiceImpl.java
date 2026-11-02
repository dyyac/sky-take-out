package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 统计指定时间区域的营业额数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计结果
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while(!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应日期
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存放每天的营业额数据
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dataList){
            //根据日期查询营业额,状态为已完成的订单
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            //判断营业额是否为null
            if(turnover == null){
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }

        String dateList = StringUtils.join(dataList, ",");

        return TurnoverReportVO.builder()
                .dateList(dateList)
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }
    /**
     * 统计指定时间区域的用户数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计结果
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while(!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应日期
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存放每天的用户总量数据
        List<Integer> totalUserList = new ArrayList<>();
        //存放每天的新增用户数据
        List<Integer> newUserList = new ArrayList<>();
        for(LocalDate date : dataList){
            //根据日期查询用户总量
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end", endTime);
            //总用户数量
            Integer totalUser = userMapper.countByMap(map);
            //新增用户数量
            map.put("begin", beginTime);

            Integer newUser = userMapper.countByMap(map);
            //判断用户总量是否为null
            if(totalUser == null){
                totalUser = 0;
            }
            //判断新增用户数量是否为null
            if(newUser == null){
                newUser = 0;
            }
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }
    /**
     * 统计指定时间区域的订单数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计结果
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while(!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应日期
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存放每天的订单总量数据
        List<Integer> totalOrderList = new ArrayList<>();
        //存放每天的有效订单数数据
        List<Integer> validOrderList = new ArrayList<>();
        //遍历datalist集合，查询每天的有效订单数和订单总数
        for(LocalDate date : dataList){
            //查询每天的订单总数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer totalOrder = getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数
            Integer validOrder = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            //判断订单总量是否为null
            if(totalOrder == null){
                totalOrder = 0;
            }
            //判断有效订单数是否为null
            if(validOrder == null){
                validOrder = 0;
            }
            totalOrderList.add(totalOrder);
            validOrderList.add(validOrder);
        }
        //计算时间区间内的订单总数
        Integer totalOrderCount = totalOrderList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单总数
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate = validOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 根据动态条件查询订单数量
     * @param map
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}

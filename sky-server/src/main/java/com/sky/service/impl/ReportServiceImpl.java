package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;

import com.sky.vo.TurnoverReportVO;
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


}

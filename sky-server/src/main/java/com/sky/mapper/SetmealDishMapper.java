package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询关联的套餐id列表
     * @param dishIds 菜品id
     * @return 关联的套餐id列表
     */
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);
}

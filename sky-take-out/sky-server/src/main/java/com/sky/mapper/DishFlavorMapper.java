package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品和对应的口味 ———— 新增口味
     * @param
     */
    void insertBach(List<DishFlavor> flavors);

    /**
     * 根据 dish_id 删除口味
     * @param dishId
     */
    @Select("select * from dish_flavor where dish_id=#{dishId};")
    void deleteByDishId(Long dishId);

    /**
     * 根据 dish_id 批量删除口味
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询菜品和口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{dishId};")
    List<DishFlavor> getByDishId(Long dishId);
}

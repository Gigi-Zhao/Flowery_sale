package com.flow.mapper;

import com.github.pagehelper.Page;
import com.flow.annotation.AutoFill;
import com.flow.dto.DishPageQueryDTO;
import com.flow.entity.Dish;
import com.flow.enumeration.OperationType;
import com.flow.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量：是“分类管理 ”界面的查询操作
     * 因操作的是dish表，放在DishMapper中
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 新增菜品和对应的口味
     * @param
     */
    // 加入公共字段自动填充的自定义注解，指定操作类型为 Insert
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id=#{id};")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品
     * @param id
     */
    @Select("select * from dish where id=#{id};")
    void deleteById(Long id);

    /**
     * 根据主键批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据主键更新菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);


    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);


    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}

package com.flow.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.flow.constant.MessageConstant;
import com.flow.constant.StatusConstant;
import com.flow.dto.DishDTO;
import com.flow.dto.DishPageQueryDTO;
import com.flow.entity.Dish;
import com.flow.entity.DishFlavor;
import com.flow.exception.DeletionNotAllowedException;
import com.flow.mapper.DishFlavorMapper;
import com.flow.mapper.DishMapper;
import com.flow.mapper.SetmealDishMapper;
import com.flow.result.PageResult;
import com.flow.service.DishService;
import com.flow.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional   // 开启事务管理
    public void saveWithFlavor(DishDTO dishDTO) {

        // 对象的属性拷贝：只需要 dish 对象即可，不需要传入口味
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish); // 需要保持两个类的属性名和类型一致

        // 1. 向菜品表插入1条数据
        dishMapper.insert(dish);

        // 获取插入的dish对象的主键值，传给flavor操作
        Long dishId = dish.getId();

        // 2. 向口味表插入n条数据

        // 获取flavor数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 判断口味表是否为空
        if(flavors!=null && !flavors.isEmpty()){

            // 通过遍历给dishId赋值：即该口味对应的菜品id
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            // 向口味表插入n条数据
            dishFlavorMapper.insertBach(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        // 开始分页查询: 传入参数 page/pageSize, 函数底层是将页数存储在 threadlocal() 变量中
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        // 返回结果类型为 Page<>，Page类继承了List，表示一系列DishVO实体的集合
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        // 将page的类型转换为PageResult
        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //1. 判断当前菜品能否删除（状态为启售则不能删）
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品处于启售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //2. 判断当前菜品是否能够删除 --- 是否被套餐关联了
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            //当前菜品被套餐关联了,不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3. 删除菜品表中的菜品数据
        /*for (Long id : ids) {
            dishMapper.deleteById(id);

            //4. 删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //3.1 根据菜品id批量删除菜品数据
        //sql: delete from dish where id in (?,?,?)
        dishMapper.deleteByIds(ids);

        //3.2 根据菜品id批量删除菜品口味数据
        //sql: delete from dish_flavor where dish_id in (?,?,?)
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据菜品id查询菜品和口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {

        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据菜品id修改菜品数据及对应的口味数据
     * @param dishDTO
     * @return
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品表基本信息
        dishMapper.update(dish);

        //删除原有的口味数据: dish_flavor表
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味数据：: dish_flavor表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBach(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
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

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}

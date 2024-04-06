package com.flow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜品口味
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    // 自增主键
    private Long id;

    //菜品id
    private Long dishId;

    //口味名称
    private String name;

    //口味数据list：一个菜品可以做多个口味
    private String value;

}

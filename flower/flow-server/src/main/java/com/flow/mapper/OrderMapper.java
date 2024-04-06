package com.flow.mapper;

import com.flow.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 向订单表中插入一条数据：并获得主键值即 订单id
     * @param orders
     */
    void insert(Orders orders);
}

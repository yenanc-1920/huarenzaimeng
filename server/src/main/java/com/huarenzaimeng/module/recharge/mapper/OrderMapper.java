package com.huarenzaimeng.module.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huarenzaimeng.module.recharge.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Update("UPDATE t_order SET order_status = #{toStatus}, updated_at = NOW() " +
            "WHERE order_no = #{orderNo} AND order_status = #{fromStatus} AND deleted = 0")
    int updateStatus(@Param("orderNo") String orderNo,
                     @Param("fromStatus") String fromStatus,
                     @Param("toStatus") String toStatus);
}

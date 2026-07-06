package com.zhenyu.drama.mapper;

import com.github.pagehelper.Page;
import com.zhenyu.pojo.dto.OrderPageQueryDTO;
import com.zhenyu.pojo.vo.OrderDetailVO;
import com.zhenyu.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper {
    Page<OrderVO> conditionSearch(OrderPageQueryDTO orderPageQueryDTO);

    @Select("SELECT o.*, u.nickname AS userName, u.phone AS userPhone, d.title AS dramaTitle, " +
            "GROUP_CONCAT(od.episode_id) AS episodeIds " +
            "FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN order_detail od ON o.id = od.order_id " +
            "LEFT JOIN drama d ON od.drama_id = d.id " +
            "WHERE o.id = #{id} " +
            "GROUP BY o.id")
    OrderDetailVO getOrderDetailById(@Param("id") Long id);


    BigDecimal getRevenue(@Param("begin") String begin, @Param("end") String end);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 2 AND DATE(create_time) = CURDATE()")
    BigDecimal getTodayRevenue();

    Integer getOrderCount(@Param("begin") String begin, @Param("end") String end);
}

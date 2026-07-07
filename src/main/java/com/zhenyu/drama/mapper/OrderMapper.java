package com.zhenyu.drama.mapper;

import com.github.pagehelper.Page;
import com.zhenyu.pojo.dto.OrderPageQueryDTO;
import com.zhenyu.pojo.entity.Order;
import com.zhenyu.pojo.entity.OrderDetail;
import com.zhenyu.pojo.vo.OrderDetailVO;
import com.zhenyu.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

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

    @Insert("insert into orders (order_no, user_id, total_amount, pay_method, pay_status, status, remark, create_time, update_time) " +
            "values (#{orderNo}, #{userId}, #{totalAmount}, #{payMethod}, #{payStatus}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    @Insert("insert into order_detail (order_id, item_type, drama_id, episode_id, item_name, price, create_time) " +
            "values (#{orderId}, #{itemType}, #{dramaId}, #{episodeId}, #{itemName}, #{price}, #{createTime})")
    void insertDetail(OrderDetail orderDetail);

    @Select("select * from orders where order_no = #{orderNo}")
    Order getByOrderNo(String orderNo);

    @Update("update orders set pay_status = #{payStatus}, status = #{status}, pay_time = #{payTime}, update_time = #{updateTime} where id = #{id}")
    void update(Order order);

    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getDetailsByOrderId(Long orderId);
}

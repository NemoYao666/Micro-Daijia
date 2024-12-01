package com.atguigu.daijia.map.repository;

import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//继承MongoRepository接口类型<数据类型, 主键类型>
@Repository
public interface OrderServiceLocationRepository extends MongoRepository<OrderServiceLocation, String> {

    List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}

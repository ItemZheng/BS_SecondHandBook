package com.bs.book.dal;

import com.bs.book.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Order getOrderByIdAndRemoved(long id, boolean removed);
}

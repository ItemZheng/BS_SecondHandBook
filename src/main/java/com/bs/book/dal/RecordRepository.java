package com.bs.book.dal;

import com.bs.book.domain.order.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface RecordRepository extends JpaRepository<Record, Long> {
    ArrayList<Record> getAllByOrderIdAndRemoved(long orderId, boolean removed);
}

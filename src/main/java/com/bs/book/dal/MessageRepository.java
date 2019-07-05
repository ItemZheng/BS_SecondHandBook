package com.bs.book.dal;


import com.bs.book.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface MessageRepository extends JpaRepository<Message, Long> {

    ArrayList<Message> getTop40ByToIdAndFromIdAndRemovedAndIdIsLessThanOrderByCreateTimeAsc(
            long to, long from, boolean removed, long id
    );

    ArrayList<Message> getTop40ByToIdAndFromIdAndRemovedOrderByCreateTimeAsc(
            long to, long from, boolean removed
    );

    ArrayList<Message> getAllByToIdAndRemovedOrderByCreateTimeAsc(long to, boolean removed);
}

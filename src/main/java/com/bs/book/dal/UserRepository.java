package com.bs.book.dal;

import com.bs.book.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByNameAndRemoved(String name, boolean removed);
    User findByEmailAndRemoved(String email, boolean removed);
    User findByIdAndRemoved(long id, boolean removed);

    @Modifying
    @Query(value = "UPDATE user_account SET name = ?1, qq = ?2, wx = ?3, phone = ?4, modify_time = ?5 WHERE id = ?6", nativeQuery = true)
    void updateInfo(String name, String qq, String wx, String phone, Date modify_time, long id);
}

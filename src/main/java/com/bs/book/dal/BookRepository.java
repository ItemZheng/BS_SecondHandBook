package com.bs.book.dal;

import com.bs.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface BookRepository extends JpaRepository<Book,Long> {
    Book getBookByIdAndRemoved(long id, boolean removed);
    ArrayList<Book> getBooksByUserIdAndStatusAndRemovedOrderByModifyTimeDesc(long userId, int status, boolean removed);
    ArrayList<Book> getBooksByUserIdAndStatusAndRemovedOrderByCreateTimeDesc(long userId, int status, boolean removed);

    @Query(value = "SELECT t from Book t WHERE t.bookType = ?1 and (t.isbn LIKE %?2% or t.bookType LIKE %?2% " +
            "or t.title LIKE %?2% or t.author LIKE %?2% or t.publisher LIKE %?2%) " +
            "and t.removed = 0 and t.status = 0 and t.aim = ?3" )
    ArrayList<Book> queryBooksWithTypeKeyAim(String type, String keyword, int aim);

    @Query(value = "SELECT t from Book t WHERE (t.isbn = ?1 or t.bookType LIKE %?1% " +
            "or t.title LIKE %?1% or t.author LIKE %?1% or t.publisher LIKE %?1%) " +
            "and t.removed = 0 and t.status = 0 and t.aim = ?2")
    ArrayList<Book> queryBooksWithKeyAim(String keyword, int aim);

    @Query(value = "SELECT t from Book t WHERE t.bookType = ?1 and t.removed = 0 and t.status = 0 and t.aim = ?2")
    ArrayList<Book> queryBooksWithTypeAim(String type, int aim);

    @Query(value = "SELECT t from Book t WHERE t.removed = 0 and t.status = 0 and t.aim = ?1")
    ArrayList<Book> queryBooksWithAim(int aim);

    ArrayList<Book> getBooksByUserIdAndRemovedOrderByCreateTimeDesc(long userId, boolean removed);
}

package com.bs.book.controller;

import com.bs.book.annotation.LoginIgnore;
import com.bs.book.domain.Book;
import com.bs.book.service.book.BookService;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/book")
public class BookController extends BaseController{
    @Resource
    BookService bookService;

    @RequestMapping("/queryIsbn")
    @LoginIgnore
    public Object queryIsbn(String isbn){
        try {
            return buildSuccessResp(bookService.getDoubanBookByISBN(isbn));
        } catch (ServiceException e){
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/add")
    public Object add(String description, String isbn, String bookType, float sellPrice, String currentImg, int aim){
        try {
            bookService.addBook(getUser().getId(), description, isbn, bookType, sellPrice, currentImg, aim);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("ADD BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/queryById")
    public Object queryById(long id){
        Book book = bookService.getBookById(id);
        if(book == null){
            return buildResponse(ErrorEnum.ERROR_BOOK_NOT_FOUND);
        }
        return buildSuccessResp(book);
    }

    @RequestMapping("/delete")
    public Object delete(long id){
        try {
            bookService.deleteBook(id, getUser().getId());
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("DELETE BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/update")
    public Object update(long id, String description, String bookType, float sellPrice, String currentImg){
        try {
            bookService.updateBook(getUser().getId(), id, description, bookType, sellPrice, currentImg);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("UPDATE BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/querySelfBooks")
    public Object querySelfBooks(int status){
        try {
            return buildSuccessResp(bookService.querySelfBooks(getUser().getId(), status));
        } catch (ServiceException e){
            log.error("QUERY BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/queryAllSelfBooks")
    public Object queryAllSelfBooks(int status){
        try {
            return buildSuccessResp(bookService.queryAllSelfBooks(getUser().getId()));
        } catch (ServiceException e){
            log.error("QUERY BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/queryBooks")
    public Object queryBooks(String type, int order, String keyword, int aim, boolean asc){
        try {
            return buildSuccessResp(bookService.queryBooks(type, order, keyword, aim, asc));
        } catch (ServiceException e){
            log.error("Query BOOK ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }
}

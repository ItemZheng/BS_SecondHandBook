package com.bs.book.service.book;

import com.bs.book.dal.BookRepository;
import com.bs.book.domain.Book;
import com.bs.book.domain.douban.DoubanBook;
import com.bs.book.domain.douban.DoubanResponse;
import com.bs.book.util.Constant;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import com.bs.book.util.Util;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class BookService {
    // todo 获取书籍详情时，如果处于出售中/交易成功，则查询书籍出售信息；否则，仅仅显示基本信息，可以修改

    @Value("${douban.apiKey}")
    String doubanApiKey;

    @Value("${douban.apiUrl}")
    String doubanApiUrl;

    @Value("${douban.argApiKey}")
    String doubanArgApiKeyName;

    @Resource
    BookRepository bookDb;

    @Resource
    Gson gson;

    public void addBook(Long userId, String description, String isbn,
                        String bookType, float sellPrice, String currentImg, int aim) throws ServiceException{
        // Check
        if(sellPrice <= 0){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_PRICE_INVALID);
        }
        if(aim != Constant.Book_Aim_Sell && aim != Constant.Book_Aim_Buy){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_PARAMENT_INVALID);
        }

        // Get Douban Book
        DoubanBook doubanBook = getDoubanBookByISBN(isbn);
        Book book = parseDoubanBook(doubanBook);

        // Set BookINFO
        book.setUserId(userId);
        book.setDescription(Util.limitStringLength(description, 4000));
        book.setIsbn(isbn);
        book.setBookType(BookType.parseType(bookType));
        book.setCurrentImg(currentImg);
        book.setSellPrice(sellPrice);
        book.setAim(aim);
        Date dateNow = new Date();
        book.setCreateTime(dateNow);
        book.setModifyTime(dateNow);
        book.setRemoved(false);
        book.setStatus(Constant.Book_Status_Origin);

        // save book
        saveBook(book);
    }

    public void deleteBook(long id, long userId)throws ServiceException{
        // Get book
        Book book = getBookById(id);
        if(book == null){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_FOUND);
        }

        // auth and validate
        auth(book, userId);
        validateBookStatus(book);

        // removed
        book.setRemoved(true);
        book.setModifyTime(new Date());
        saveBook(book);
    }

    public ArrayList<Book> querySelfBooks(long userId, int status) throws ServiceException{
        try{
            ArrayList<Book> books;
            if(status == Constant.Book_Status_Origin){
                books = bookDb.getBooksByUserIdAndStatusAndRemovedOrderByCreateTimeDesc(userId, status, false);
            } else{
                books = bookDb.getBooksByUserIdAndStatusAndRemovedOrderByModifyTimeDesc(userId, status, false);
            }
            if(books == null){
                books = new ArrayList<>();
            }
            return books;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    public ArrayList<Book> queryAllSelfBooks(long userId) throws ServiceException{
        try{
            ArrayList<Book> books = bookDb.getBooksByUserIdAndRemovedOrderByCreateTimeDesc(userId, false);
            if(books == null){
                books = new ArrayList<>();
            }
            return books;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    public void updateBook(long userId, long id, String description, String bookType, float sellPrice, String currentImg) throws ServiceException{
        // Get Book
        Book book = getBookById(id);
        if(book == null){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_FOUND);
        }

        // auth
        auth(book, userId);
        validateBookStatus(book);

        // update
        book.setDescription(Util.limitStringLength(description, 4000));
        book.setBookType(BookType.parseType(bookType));
        book.setCurrentImg(currentImg);
        book.setSellPrice(sellPrice);

        // update time
        book.setModifyTime(new Date());
        saveBook(book);
    }

    private void auth(Book book, long userId)throws ServiceException{
        if(book.getUserId() != userId){
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }
    }

    public ArrayList<Book> queryBooks(String type, int order, String keyword, int aim, boolean asc) throws ServiceException{
        // adjust type
        if(type == null){
            type = "";
        } else if(!"".equals(type)){
            type = BookType.parseType(type);
        }

        // adjust keyword
        if(keyword == null){
            keyword = "";
        }

        // query
        if(aim != Constant.Book_Aim_Buy && aim != Constant.Book_Aim_Sell){
            throw new ServiceException(ErrorEnum.ERROR_QUERY_PARAMENT_INVALID);
        }

        // query
        ArrayList<Book> books;
        if(type.equals("") && keyword.equals("")){
            books = bookDb.queryBooksWithAim(aim);
        } else if(type.equals("")){
            books = bookDb.queryBooksWithKeyAim(keyword, aim);
        } else if(keyword.equals("")){
            books = bookDb.queryBooksWithTypeAim(type, aim);
        } else {
            books = bookDb.queryBooksWithTypeKeyAim(type, keyword, aim);
        }
        // adjust
        if(books == null){
            books = new ArrayList<>();
        }

        // order
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if(order == Constant.BOOK_ORDER_PRICE){
                    if(o1.getSellPrice() == o2.getSellPrice()){
                        return 0;
                    } else if(o1.getSellPrice() < o2.getSellPrice() && asc || !(o1.getSellPrice() < o2.getSellPrice()) && !asc){
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    if(o1.getCreateTime().equals(o2.getCreateTime())){
                        return 0;
                    } else if(o1.getCreateTime().before(o2.getCreateTime()) && asc ||
                            !(o1.getCreateTime().before(o2.getCreateTime())) && !asc){
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
        return books;
    }

    public Book getBookById(Long id){
        return bookDb.getBookByIdAndRemoved(id, false);
    }

    private void saveBook(Book book)throws ServiceException{
        try {
            bookDb.save(book);
        } catch (Exception e){
            log.error("Save Book ERROR: " + e.getMessage());
            throw new ServiceException(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    private void validateBookStatus(Book book) throws ServiceException{
        if(book.getStatus() != Constant.Book_Status_Origin){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_STATUS_INVALID);
        }
    }


    private Book parseDoubanBook(DoubanBook doubanBook){
        Book book = new Book();
        // title
        book.setTitle(Util.limitStringLength(doubanBook.getTitle(), 100));
        // author
        if(doubanBook.getAuthor() != null){
            book.setAuthor(Util.limitStringLength(doubanBook.getAuthor().get(0), 100));
        } else{
            book.setAuthor("");
        }
        // oriPrice
        book.setOriPrice(Util.limitStringLength(doubanBook.getPrice(), 100));
        // publisher
        book.setPublisher(Util.limitStringLength(doubanBook.getPublisher(), 100));
        // OriImage
        book.setOriImg(Util.limitStringLength(doubanBook.getImage(), 1000));
        // Summary
        book.setSummary(Util.limitStringLength(doubanBook.getSummary(), 4000));
        // Catalog
        book.setCatalog(Util.limitStringLength(doubanBook.getCatalog(), 4000));
        if(doubanBook.getRating() != null){
            book.setRating(doubanBook.getRating().getAverage());
        } else {
            book.setRating("");
        }
        return book;
    }

    public DoubanBook getDoubanBookByISBN(String isbn) throws ServiceException {
        // douban api uri
        String uri = doubanApiUrl + isbn;

        // douban api args
        Map<String, String> args= new HashMap<>();
        args.put(doubanArgApiKeyName, doubanApiKey);

        // response
        String res = Util.httpsGet(uri, args);
        try {
            return gson.fromJson(res, DoubanBook.class);
        } catch (Exception e){
            e.printStackTrace();
            try {
                DoubanResponse doubanResponse = gson.fromJson(res, DoubanResponse.class);
                if(doubanResponse.getCode() == 6000){
                    // Book Not Found
                    throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_FOUND);
                }
            } catch (Exception ignore){

            }
            throw new ServiceException(ErrorEnum.ERROR_UNEXPECTED_API_RESPONSE);
        }
    }

}

package book.example.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import book.example.entity.Book;
import book.example.payload.BookRequest;
import book.example.repository.BookRepository;

@Service
public class BookService {

	Logger _log = LoggerFactory.getLogger(BookService.class);
	
    @Autowired
    private BookRepository bookRepository;
    
    @Cacheable(value = "booksList", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public List<Book> getAllBooksList(Pageable pageable) {
    	_log.info("fetching all book data from db, page:"+pageable.getPageNumber()+" | size:"+pageable.getPageSize()
    		+" | sort:"+pageable.getSort());
        Page<Book> page = bookRepository.findAll(pageable);
        // Convert Page to List
        return page.getContent();
    }
    
    @Cacheable(value = "booksCount")
    public long getBooksCount() {
        return bookRepository.count();
    }
    
    @CacheEvict(value = {"booksList", "booksCount"}, allEntries = true)
    public Book addBook(BookRequest newBook) {
    	Book book = new Book();
    	book.setTitle(newBook.getTitle());
    	book.setAuthor(newBook.getAuthor());
    	book.setPublishedDate(newBook.getPublishedDate());
    	book.setPages(newBook.getPages());
    	book.setIsbn(newBook.getIsbn());
    	book.setStock(newBook.getStock());
    	book.setPrice(newBook.getPrice());
    	book.setDiscount(newBook.getDiscount());
    	book.setDescription(newBook.getDescription());
    	book.setBookCondition(newBook.getBookCondition());
    	book.setActive(newBook.isActive());
    	book.setCreatedBy(0);
    	book.setCreatedDate(new Date());
    	book.setModifiedBy(0);
    	book.setModifiedDate(new Date());
    	_log.info("save book data to db");
    	return bookRepository.save(book);
    }
    
    @CacheEvict(value = {"bookDetails", "booksList"}, key = "#id", allEntries = true)
    public Optional<Book> updateBook(long id, BookRequest updatedBook) {
    	Optional<Book> existingBookOpt = bookRepository.findById(id);
    	if (existingBookOpt.isPresent()) {
    		Book existingBook = existingBookOpt.get();
    		existingBook.setTitle(updatedBook.getTitle());
    		existingBook.setAuthor(updatedBook.getAuthor());
    		existingBook.setPublishedDate(updatedBook.getPublishedDate());
    		existingBook.setPages(updatedBook.getPages());
    		existingBook.setIsbn(updatedBook.getIsbn());
    		existingBook.setStock(updatedBook.getStock());
    		existingBook.setPrice(updatedBook.getPrice());
    		existingBook.setDiscount(updatedBook.getDiscount());
    		existingBook.setDescription(updatedBook.getDescription());
    		existingBook.setBookCondition(updatedBook.getBookCondition());
        	existingBook.setActive(updatedBook.isActive());
    		existingBook.setModifiedBy(0);
    		existingBook.setModifiedDate(new Date());
        	_log.info("update book data to db");
        	bookRepository.save(existingBook);
        	return Optional.of(existingBook);
    	} else {
    		return Optional.empty();
    	}
    }
    
    @Cacheable(value = "bookDetails", key = "#id", unless = "#result == null")
    public Optional<Book> getBookDetail(long id) {
    	_log.info("fetching book data from db");
    	return bookRepository.findById(id).or(() -> Optional.empty());
    }
}
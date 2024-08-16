package book.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import book.example.entity.Book;
import book.example.entity.BookConditionEnum;
import book.example.payload.BookRequest;
import book.example.repository.BookRepository;

public class BookServiceTest {

	@InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;
    
    private Book book;
    
    private BookRequest bookRequest;
    
    private List<Book> bookList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inisialisasi mock objects
        // book
        book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setAuthor("Book Author");
        book.setPublishedDate(new Date(1723534282));
        book.setPages(100);
        book.setIsbn("9786025385889");
        book.setStock(20);
        book.setPrice(new BigDecimal(105000));
        book.setDiscount(new BigDecimal(0));
        book.setDescription("Book Description");
        book.setBookCondition(BookConditionEnum.NEW);
        book.setActive(true);
        
        // Add a mock book to the list
        bookList.add(book);
        
        // book request
        bookRequest = new BookRequest();
        bookRequest.setTitle("New Book Title");
        bookRequest.setAuthor("New Book Author");
        bookRequest.setPublishedDate(new Date(1723534282));
        bookRequest.setPages(100);
        bookRequest.setIsbn("9786025385889");
        bookRequest.setStock(20);
        bookRequest.setPrice(new BigDecimal(105000));
        bookRequest.setDiscount(new BigDecimal(0));
        bookRequest.setDescription("New Book Description");
        bookRequest.setBookCondition(BookConditionEnum.NEW);
        bookRequest.setActive(true);
    }
    
    @Test
    void testGetAllBooksList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(bookList);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        List<Book> result = bookService.getAllBooksList(pageable);

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetBooksCount() {
        when(bookRepository.count()).thenReturn(100L);

        long result = bookService.getBooksCount();

        assertEquals(100L, result);
        verify(bookRepository, times(1)).count();
    }

    @Test
    void testAddBook() {
        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setTitle(bookRequest.getTitle()); 
        newBook.setAuthor(bookRequest.getAuthor());
        newBook.setPublishedDate(bookRequest.getPublishedDate());
        newBook.setPages(bookRequest.getPages());
        newBook.setIsbn(bookRequest.getIsbn());
        newBook.setStock(bookRequest.getStock());
        newBook.setPrice(bookRequest.getPrice());
        newBook.setDiscount(bookRequest.getDiscount());
        newBook.setDescription(bookRequest.getDescription());
        newBook.setBookCondition(bookRequest.getBookCondition());
        newBook.setActive(bookRequest.isActive());

        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        Book result = bookService.addBook(bookRequest);

        // Assert
        assertEquals("New Book Title", result.getTitle());
        assertEquals("New Book Author", result.getAuthor());
        assertEquals(new Date(1723534282), result.getPublishedDate());
        assertEquals(100, result.getPages());
        assertEquals("9786025385889", result.getIsbn());
        assertEquals(20, result.getStock());
        assertEquals(new BigDecimal(105000), result.getPrice());
        assertEquals(new BigDecimal(0), result.getDiscount());
        assertEquals("New Book Description", result.getDescription());
        assertEquals(BookConditionEnum.NEW, result.getBookCondition());
        assertEquals(true, result.isActive());
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    void testUpdateBook_BookExists() {
        long bookId = 1L;

        BookRequest updatedBookRequest = new BookRequest();
        updatedBookRequest.setTitle("Updated Title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> updatedBookOpt = bookService.updateBook(bookId, updatedBookRequest);

        // Assert
        assertTrue(updatedBookOpt.isPresent());
        Book updatedBook = updatedBookOpt.get();
        assertEquals("Updated Title", updatedBook.getTitle());
        verify(bookRepository, times(1)).save(book);
        verify(bookRepository, times(1)).findById(bookId);
    }
    
    @Test
    void testUpdateBook_BookDoesNotExist() {
        long bookId = 1L;
        BookRequest updatedBookRequest = new BookRequest();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Optional<Book> updatedBookOpt = bookService.updateBook(bookId, updatedBookRequest);

        // Assert
        assertFalse(updatedBookOpt.isPresent());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookRepository, times(1)).findById(bookId);
    }
    
    @Test
    void testGetBookDetail_BookExists() {
        long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> bookOpt = bookService.getBookDetail(bookId);
        
        // Assert
        assertTrue(bookOpt.isPresent());
        assertEquals("Book Title", bookOpt.get().getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookDetail_BookDoesNotExist() {
        long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Optional<Book> bookOpt = bookService.getBookDetail(bookId);

        // Assert
        assertFalse(bookOpt.isPresent());
        verify(bookRepository, times(1)).findById(bookId);
    }
}
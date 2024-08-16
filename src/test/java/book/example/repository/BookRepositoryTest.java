package book.example.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import book.example.entity.Book;
import book.example.entity.BookConditionEnum;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book;
    
    @BeforeEach
    void setUp() {
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
    }
    
    @Test
    public void testSaveAndFindById() {
        Book savedBook = bookRepository.save(book);

        // find by ID
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Book Title");
        assertThat(foundBook.get().getAuthor()).isEqualTo("Book Author");
    }

    @Test
    public void testFindAll() {
        Book book2 = new Book();
        book2.setTitle("Book Title 2");
        book2.setAuthor("Book Author 2");

        bookRepository.save(book);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Book Title", "Book Title 2");
    }
    
    @Test
    public void testFindByTitle() {
        bookRepository.save(book);

        // find by title
        Optional<Book> foundBook = bookRepository.findByTitle("Book Title");

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Book Title");
        assertThat(foundBook.get().getAuthor()).isEqualTo("Book Author");
    }
    
    @Test
    public void testBookNotFoundByTitle() {
        // find non exist book
        Optional<Book> foundBook = bookRepository.findByTitle("Nonexistent Book");

        assertThat(foundBook).isNotPresent();
    }
    
    @Test
    public void testFindByAuthor() {
    	Book book2 = new Book();
        book2.setTitle("Book Title 2");
        book2.setAuthor("Book Author");

        bookRepository.save(book);
        bookRepository.save(book2);

        // find by author
        List<Book> books = bookRepository.findByAuthor("Book Author");
        
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getAuthor)
            .contains("Book Author");
    }
}
package book.example.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import book.example.controller.BookController;
import book.example.entity.Book;
import book.example.entity.BookConditionEnum;
import book.example.payload.BookResponse;

public class BookModelAssemblerTest {

	private BookModelAssembler assembler;

	private Book book;
	
    @BeforeEach
    public void setUp() {
        assembler = new BookModelAssembler();
        
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
    public void testToModel() throws Exception {
        BookResponse bookModel = assembler.toModel(book);

        // Assert that links are correctly added
        assertThat(bookModel.getLinks()).isNotEmpty();
        assertThat(bookModel.getLink("self")).isPresent();
        assertThat(bookModel.getLink("self").get().getHref())
                .isEqualTo(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class)
                        .getBookDetail(book.getId())).toUri().toString());
    }
}
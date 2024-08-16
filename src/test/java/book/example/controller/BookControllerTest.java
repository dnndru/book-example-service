package book.example.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import book.example.entity.Book;
import book.example.entity.BookConditionEnum;
import book.example.payload.BookRequest;
import book.example.payload.BookResponse;
import book.example.repository.BookModelAssembler;
import book.example.service.BookService;

//@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@ActiveProfiles("test")  // Mengaktifkan profil 'test'
public class BookControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    
    @MockBean
    private BookModelAssembler bookModelAssembler;
    
    @MockBean
    private PagedResourcesAssembler<Book> pagedResourcesAssembler;

    private Book book;
    private BookRequest bookRequest;
    private List<Book> booksList = Arrays.asList(book, book);
    private Page<Book> booksPage = new PageImpl<>(booksList);
    private BookResponse bookResponse = new BookResponse();
    
    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        bookRequest = new BookRequest();
        bookRequest.setTitle("New Book");
        bookRequest.setAuthor("Book Author");
        bookRequest.setPublishedDate(new Date(1723534282));
        bookRequest.setPages(100);
        bookRequest.setIsbn("9786025385889");
        bookRequest.setStock(20);
        bookRequest.setPrice(new BigDecimal(105000));
        bookRequest.setDiscount(new BigDecimal(0));
        bookRequest.setDescription("Book Description");
        bookRequest.setBookCondition(BookConditionEnum.NEW);
        bookRequest.setActive(true);
    }
    
    @Test
    public void testGetAllBooks_Success() throws Exception {
        when(bookService.getAllBooksList(any(Pageable.class))).thenReturn(booksList);
        when(bookService.getBooksCount()).thenReturn(2L);
        when(pagedResourcesAssembler.toModel(eq(booksPage), any(BookModelAssembler.class))).thenReturn(null);

        mockMvc.perform(get("/api/books/all")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(status().isOk());

        verify(bookService).getAllBooksList(any(Pageable.class));
    }
    
    @Test
    public void testGetAllBooks_Error() throws Exception {
        when(bookService.getAllBooksList(any(Pageable.class))).thenThrow(new NullPointerException("Null value encountered"));

        mockMvc.perform(get("/api/books/all")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,asc"))
                .andExpect(status().isInternalServerError());

        verify(bookService).getAllBooksList(any(Pageable.class));
    }
    
    @Test
    public void testAddBook_Success() throws Exception {
        when(bookService.addBook(any(BookRequest.class))).thenReturn(book);
        when(bookModelAssembler.toModel(any(Book.class))).thenReturn(bookResponse);

        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Book successfully created!"));

        verify(bookService).addBook(any(BookRequest.class));
    }
    
    @Test
    public void testAddBook_NotValid() throws Exception {
        when(bookService.addBook(any(BookRequest.class))).thenReturn(book);

        // not valid request
        bookRequest.setPages(0);
        
        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
        .andExpect(status().isBadRequest()) // Memastikan status yang dikembalikan adalah 400 Bad Request
//        .andExpect(jsonPath("$.errors", containsString("Invalid: Pages must be greater than zero")));
        .andExpect(jsonPath("$.errors", hasItem("Invalid: Pages must be greater than zero"))); // Verifikasi pesan error untuk field 'pages'

        verify(bookService, never()).addBook(any(BookRequest.class)); // Verifikasi bahwa service tidak dipanggil jika request tidak valid
    }
    
    @Test
    public void testAddBook_MessageNotReadableException() throws Exception {
        when(bookService.addBook(any(BookRequest.class))).thenReturn(book);

        // JSON request body with an invalid enum value
        String invalidJsonRequest = "{"
                + "\"title\": \"New Book\","
                + "\"author\": \"Book Author\","
                + "\"pages\": 100,"
                + "\"bookCondition\": \"INVALID_CONDITION\""
                + "}";
        
        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJsonRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Invalid value for field 'bookCondition'")))
        .andExpect(jsonPath("$.message", containsString("Allowed values are: NEW, USED")));

        verify(bookService, never()).addBook(any(BookRequest.class)); // Verifikasi bahwa service tidak dipanggil jika request tidak valid
    }
    
    @Test
    public void testAddBook_Error() throws Exception {
        when(bookService.addBook(any(BookRequest.class))).thenThrow(new NullPointerException("Null value encountered"));

        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
                .andExpect(status().isInternalServerError());

        verify(bookService).addBook(any(BookRequest.class));
    }
    
    @Test
    public void testGetBookDetail_Success() throws Exception {
        when(bookService.getBookDetail(anyLong())).thenReturn(Optional.of(book));
        when(bookModelAssembler.toModel(any(Book.class))).thenReturn(bookResponse);

        mockMvc.perform(get("/api/books/detail/{bookId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        verify(bookService).getBookDetail(anyLong());
    }

    @Test
    public void testGetBookDetail_NotFound() throws Exception {
        Long bookId = 1L;

        when(bookService.getBookDetail(bookId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/detail/{bookId}", 1L))
    		.andExpect(status().isNotFound());
    }
    
    @Test
    public void testUpdateBook_Success() throws Exception {
        when(bookService.updateBook(anyLong(), any(BookRequest.class))).thenReturn(Optional.of(book));
        when(bookModelAssembler.toModel(any(Book.class))).thenReturn(bookResponse);

        mockMvc.perform(put("/api/books/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book successfully updated!"));

        verify(bookService).updateBook(anyLong(), any(BookRequest.class));
    }

    @Test
    public void testUpdateBook_BookNotFound() throws Exception {
        when(bookService.updateBook(anyLong(), any(BookRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/books/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
                .andExpect(status().isNotFound());

        verify(bookService).updateBook(anyLong(), any(BookRequest.class));
    }
    
    private static String asJsonString(final Object obj) {
    	try {
    		return new ObjectMapper().writeValueAsString(obj);
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
}
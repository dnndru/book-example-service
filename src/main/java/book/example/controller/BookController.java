package book.example.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import book.example.entity.Book;
import book.example.exception.BookNotFoundException;
import book.example.payload.BookRequest;
import book.example.payload.BookResponse;
import book.example.payload.ResponseDto;
import book.example.repository.BookModelAssembler;
import book.example.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	Logger _log = LoggerFactory.getLogger(BookController.class);
	
	@Autowired
    private BookService bookService;
	
    @Autowired
    private BookModelAssembler bookModelAssembler;
    
    @Autowired
    private PagedResourcesAssembler<Book> pagedResourcesAssembler;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) throws Exception {
    	try {
    		Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
            
            // Get List and metadata from cache
            List<Book> booksList = bookService.getAllBooksList(pageable);
            long totalElements = bookService.getBooksCount();
            // Convert List to Page
            Page<Book> booksPage = new PageImpl<>(booksList, pageable, totalElements);
            PagedModel<BookResponse> collectionModel = pagedResourcesAssembler
            		.toModel(booksPage, bookModelAssembler);
            
            return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    	} catch (Exception e) {
    		_log.info("error while getAllBooks:" + e.getMessage());
    		throw new Exception(e.getMessage());
		}
    }
    
	@PostMapping("/add")
	public ResponseEntity<?> addBook(@RequestBody @Valid BookRequest book) throws Exception {
		ResponseDto dto = new ResponseDto();
		try {
			Book savedBook = bookService.addBook(book);
			BookResponse model = bookModelAssembler.toModel(savedBook);
			
			dto.setTimestamp(new Date());
			dto.setStatus(HttpStatus.CREATED.value());
			dto.setMessage("Book successfully created!");
			dto.setData(model);

			return new ResponseEntity<ResponseDto>(dto, HttpStatus.CREATED);
		} catch (Exception e) {
			_log.info("error while addBook:" + e.getMessage());
			throw new Exception(e.getMessage());
		}
    }
	
	@GetMapping("/detail/{bookId}")
	public ResponseEntity<?> getBookDetail(@PathVariable long bookId) throws Exception {
		ResponseDto dto = new ResponseDto();
		try {
			Optional<Book> bookOptional = bookService.getBookDetail(bookId);
			if (bookOptional.isPresent()) {
				Book book = bookOptional.get();
				BookResponse model = bookModelAssembler.toModel(book);
				
				dto.setTimestamp(new Date());
				dto.setStatus(HttpStatus.OK.value());
				dto.setMessage("success");
				dto.setData(model);
				
				return new ResponseEntity<ResponseDto>(dto, HttpStatus.OK);
			} else {
				throw new BookNotFoundException("Book not found");
			}
		} catch (Exception e) {
			_log.info("error while getBookDetail:" + e.getMessage());
			if (e instanceof BookNotFoundException) {
				throw new BookNotFoundException(e.getMessage());
			} else {
				throw new Exception(e.getMessage());
			}
		}
	}
	
	@PutMapping("/update/{id}")
    public ResponseEntity<?> updateBook( @PathVariable Long id, 
    		@RequestBody @Valid BookRequest updatedBook) throws Exception {
		ResponseDto dto = new ResponseDto();
		try {
			Optional<Book> updatedBookOpt = bookService.updateBook(id, updatedBook);
			if (updatedBookOpt.isPresent()) {
	            Book book = updatedBookOpt.get();
	            BookResponse model = bookModelAssembler.toModel(book);

	            dto.setTimestamp(new Date());
				dto.setStatus(HttpStatus.OK.value());
				dto.setMessage("Book successfully updated!");
				dto.setData(model);
				
				return new ResponseEntity<ResponseDto>(dto, HttpStatus.OK);
	        } else {
	        	throw new BookNotFoundException("Book not found");
	        }
		} catch (Exception e) {
			_log.info("error while updateBook:" + e.getMessage());
			if (e instanceof BookNotFoundException) {
				throw new BookNotFoundException(e.getMessage());
			} else {
				throw new Exception(e.getMessage());
			}
		}
	}
}
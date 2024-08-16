package book.example.repository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import book.example.controller.BookController;
import book.example.entity.Book;
import book.example.payload.BookResponse;

@Component
public class BookModelAssembler extends RepresentationModelAssemblerSupport<Book, BookResponse> {

	Logger _log = LoggerFactory.getLogger(BookModelAssembler.class);
	
	public BookModelAssembler() {
        super(BookController.class, BookResponse.class);
    }
	
	@Override
	public BookResponse toModel(Book entity) {
		BookResponse dto = instantiateModel(entity);
        try {
			dto.add(linkTo(methodOn(BookController.class)
			        .getBookDetail(entity.getId()))
			        .withSelfRel());
			
			//dto.setId(entity.getId());
	        dto.setTitle(entity.getTitle());
	        dto.setAuthor(entity.getAuthor());
	        dto.setPublishedDate(entity.getPublishedDate());
	        dto.setPages(entity.getPages());
	        dto.setIsbn(entity.getIsbn());
	        dto.setStock(entity.getStock());
	        dto.setPrice(entity.getPrice());
	        dto.setDiscount(entity.getDiscount());
	        dto.setDescription(entity.getDescription());
	        dto.setBookCondition(entity.getBookCondition());
	        dto.setActive(entity.isActive());
	        dto.setCreatedBy(entity.getCreatedBy());
	        dto.setCreatedDate(entity.getCreatedDate());
	        dto.setModifiedBy(entity.getModifiedBy());
	        dto.setModifiedDate(entity.getModifiedDate());
		} catch (Exception e) {
			_log.info("error toModel:" + e.getMessage());
		}

        return dto;
	}
}
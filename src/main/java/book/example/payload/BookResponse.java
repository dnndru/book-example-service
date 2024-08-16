package book.example.payload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import book.example.entity.BookConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "books")
public class BookResponse extends RepresentationModel<BookRequest> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String title;
    
    private String author;
    
    private Date publishedDate;
    
    private int pages;
    
    private String isbn;
    
    private int stock;
    
    private BigDecimal price;
    
    private BigDecimal discount;
    
    private String description;
    
    @Enumerated(EnumType.ORDINAL)
    private BookConditionEnum bookCondition;
    
    private boolean active;
    
	private long createdBy;
	
	private Date createdDate;
	
	private long modifiedBy;
	
	private Date modifiedDate;
}
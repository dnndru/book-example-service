package book.example.payload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
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
public class BookRequest extends RepresentationModel<BookRequest> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "Invalid: Empty title")
    @NotNull(message = "Invalid: Title is NULL")
    @Size(min = 3, message = "Invalid: Title min 3 characters")
    private String title;
    
	@NotBlank(message = "Invalid: Empty author")
    @NotNull(message = "Invalid: Author is NULL")
    @Size(min = 3, message = "Invalid: Author min 3 characters")
    private String author;
    
	@NotNull(message = "Invalid: Active status is NULL")
    private boolean active;
    
	@NotNull(message = "Invalid: Published Date is NULL")
	@DateTimeFormat
    private Date publishedDate;
    
	@Min(value = 1, message = "Invalid: Pages must be greater than zero")
    private int pages;
    
    private String isbn;
    
    @Min(value = 1, message = "Invalid: Stock must be greater than zero")
    private int stock;
    
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal discount;
    
    @NotNull(message = "Invalid: Description is NULL")
    private String description;
    
    @NotNull(message = "Invalid: Condition is NULL")
    @Enumerated(EnumType.ORDINAL)
    private BookConditionEnum bookCondition;
}
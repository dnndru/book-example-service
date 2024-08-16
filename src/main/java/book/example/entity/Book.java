package book.example.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "testing_book")
public class Book implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String title;
    
    private String author;
    
    private Date publishedDate;
    
    private int pages;
    
    private String isbn;
    
    private int stock;
    
    private BigDecimal price;
    
    private BigDecimal discount;
    
    @Column(columnDefinition="TEXT")
    private String description;
    
    @Enumerated(EnumType.ORDINAL)
    private BookConditionEnum bookCondition;
    
    private boolean active;
    
	private long createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	private long modifiedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
}
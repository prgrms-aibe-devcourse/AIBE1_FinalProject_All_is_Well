package org.example.bookmarket.book.dto;

import org.example.bookmarket.book.entity.Book;

public record BookResponse(
    Long id,
    String isbn,
    String title,
    String author,
    String publisher,
    Integer publicationYear,
    Integer newPrice,
    String description,
    String coverImageUrl
) {

    /**
     * Convert this response to a {@link Book} entity instance.
     */
    public Book toEntity() {
        return Book.builder()
                .id(id)
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .publicationYear(publicationYear)
                .newPrice(newPrice)
                .description(description)
                .coverImageUrl(coverImageUrl)
                .build();
    }

    /**
     * Build a {@link BookResponse} from a {@link Book} entity.
     */
    public static BookResponse from(Book book) {
        if (book == null) return null;
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getNewPrice(),
                book.getDescription(),
                book.getCoverImageUrl()
        );
    }
}

package org.example.bookmarket.book.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.book.dto.BookResponse;
import org.example.bookmarket.usedbook.dto.UsedBookPostRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    /**
     * 검색 결과 페이지를 보여주는 메소드
     * @param keyword 검색어
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * @return 보여줄 템플릿 파일의 이름
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {

        List<BookResponse> searchResults = List.of(
                new BookResponse(1L, "979-11-6224-484-9", "The Great Gatsby", "F. Scott Fitzgerald", "Scribner", 1925, 15000, "설명...", "https://images.unsplash.com/photo-1608649699552-e31d5a4a5a0d?q=80&w=1887&auto=format&fit=crop"),
                new BookResponse(2L, "978-0-7432-7356-5", "To Kill a Mockingbird", "Harper Lee", "J. B. Lippincott & Co.", 1960, 18000, "설명...", "https://images.unsplash.com/photo-1544716278-ca5e3ac4030e?q=80&w=1887&auto=format&fit=crop"),
                new BookResponse(3L, "978-0-452-28423-4", "1984", "George Orwell", "Secker & Warburg", 1949, 16000, "설명...", "https://images.unsplash.com/photo-1529566275382-203a95441a14?q=80&w=1887&auto=format&fit=crop"),
                new BookResponse(4L, "978-0-679-78326-8", "Pride and Prejudice", "Jane Austen", "T. Egerton", 1813, 14000, "설명...", "https://images.unsplash.com/photo-1583995831614-3818b857702f?q=80&w=1887&auto=format&fit=crop")
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("searchResults", searchResults);

        return "search-results";
    }

    /**
     * 책 상세 페이지를 보여주는 메소드
     * @param bookId URL 경로에서 넘어온 책 ID
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * @return 보여줄 템플릿 파일의 이름
     */
    @GetMapping("/used-books/{bookId}")
    public String bookDetail(@PathVariable Long bookId, Model model) {

        BookResponse book = new BookResponse(
                bookId,
                "978-0321765723",
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                "Scribner",
                1925,
                15000,
                "This book is in excellent condition, with minimal signs of wear. The pages are clean and unmarked, and the binding is tight. It's perfect for collectors or readers who appreciate a pristine copy.",
                "https://images.unsplash.com/photo-1621827942598-95392157c698?q=80&w=1887&auto=format&fit=crop"
        );

        model.addAttribute("book", book);

        return "book-detail";
    }

    /**
     * 책 등록 페이지를 보여주는 메소드
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * @return 보여줄 템플릿 파일의 이름
     */
    @GetMapping("/used-books/new")
    public String registerBookForm(Model model) {

        model.addAttribute("bookRequest", new UsedBookPostRequest(
                null, null, null, null, null, null, null,
                false, false, false, false, false,
                null, null, null, null, null
        ));
        return "book-register";
    }
}
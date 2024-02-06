package md.spring.books.controllers;

import md.spring.books.model.Book;
import md.spring.books.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("books")
public class BooksController {
    @Autowired
    private BookService bookService;

    @GetMapping("{id}") // GET localhost:8080/books/23
    public ResponseEntity<Book> getBookInfo(@PathVariable Long id){
        Book book = bookService.findBook(id);
        if(book == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }
    @GetMapping// GET localhost:8080/books
    public ResponseEntity<Collection<Book>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping //POST  localhost:8080/books
    public Book createBook(@RequestBody Book book){
        return bookService.createBook(book);
    }

    @PutMapping //PUT  localhost:8080/books
    public ResponseEntity<Book> editBook(@RequestBody Book book){
        Book foundBook = bookService.editBook(book);
        if(foundBook == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundBook);
    }

    @DeleteMapping("{id}") //DELETE  localhost:8080/books/23
    public Book deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }



}

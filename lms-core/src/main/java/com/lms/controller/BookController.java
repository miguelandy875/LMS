package com.lms.controller;

import java.util.List;

import com.lms.model.Book;
import com.lms.service.BookService;
import com.lms.service.impl.BookServiceImpl;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol;
    @FXML private TableColumn<Book, String> titleCol;

    private final BookService bookService = new BookServiceImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
    }

    @FXML
    public void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        bookTable.getItems().setAll(books);
    }
}

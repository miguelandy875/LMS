package com.lms.dao;

import com.lms.model.Book;
import com.lms.model.Author;
import com.lms.model.Category;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO implements GenericDAO<Book> {
    
    private static final String INSERT_BOOK = 
        "INSERT INTO books (cat_id, book_title, book_pages, book_pub_year, status) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_BOOK = 
        "UPDATE books SET cat_id=?, book_title=?, book_pages=?, book_pub_year=?, status=? WHERE book_id=?";
    
    private static final String DELETE_BOOK = 
        "DELETE FROM books WHERE book_id=?";
    
    private static final String SELECT_BOOK_BY_ID = 
        "SELECT b.*, c.cat_name FROM books b LEFT JOIN categories c ON b.cat_id = c.cat_id WHERE b.book_id=?";
    
    private static final String SELECT_ALL_BOOKS = 
        "SELECT b.*, c.cat_name FROM books b LEFT JOIN categories c ON b.cat_id = c.cat_id ORDER BY b.book_title";
    
    private static final String SELECT_BOOKS_BY_STATUS = 
        "SELECT b.*, c.cat_name FROM books b LEFT JOIN categories c ON b.cat_id = c.cat_id WHERE b.status=? ORDER BY b.book_title";
    
    private static final String SELECT_BOOKS_BY_CATEGORY = 
        "SELECT b.*, c.cat_name FROM books b LEFT JOIN categories c ON b.cat_id = c.cat_id WHERE b.cat_id=? ORDER BY b.book_title";
    
    private static final String SEARCH_BOOKS = 
        "SELECT DISTINCT b.*, c.cat_name FROM books b " +
        "LEFT JOIN categories c ON b.cat_id = c.cat_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE LOWER(b.book_title) LIKE LOWER(?) " +
        "OR LOWER(a.author_name) LIKE LOWER(?) " +
        "OR LOWER(c.cat_name) LIKE LOWER(?) " +
        "ORDER BY b.book_title";
    
    private static final String COUNT_BOOKS = "SELECT COUNT(*) FROM books";
    private static final String COUNT_BOOKS_BY_STATUS = "SELECT COUNT(*) FROM books WHERE status=?";
    
    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    
    public BookDAO() {
        this.authorDAO = new AuthorDAO();
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    public void save(Book book) {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Insert book
            try (PreparedStatement statement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, book.getCategoryId());
                statement.setString(2, book.getTitle());
                statement.setInt(3, book.getPages());
                statement.setString(4, book.getPublicationYear());
                statement.setString(5, book.getStatus());
                
                int rowsAffected = statement.executeUpdate();
                
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            book.setBookId(generatedKeys.getInt(1));
                        }
                    }
                }
            }
            
            // Insert book-author relationships
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                saveBookAuthors(connection, book);
            }
            
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Error saving book: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Book book) {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Update book
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_BOOK)) {
                statement.setInt(1, book.getCategoryId());
                statement.setString(2, book.getTitle());
                statement.setInt(3, book.getPages());
                statement.setString(4, book.getPublicationYear());
                statement.setString(5, book.getStatus());
                statement.setInt(6, book.getBookId());
                
                statement.executeUpdate();
            }
            
            // Delete existing author relationships
            deleteBookAuthors(connection, book.getBookId());
            
            // Insert new author relationships
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                saveBookAuthors(connection, book);
            }
            
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Error updating book: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(int bookId) {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Delete author relationships first
            deleteBookAuthors(connection, bookId);
            
            // Delete book
            try (PreparedStatement statement = connection.prepareStatement(DELETE_BOOK)) {
                statement.setInt(1, bookId);
                statement.executeUpdate();
            }
            
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Error deleting book: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Book findById(int bookId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOK_BY_ID)) {
            
            statement.setInt(1, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    book.setAuthors(findAuthorsByBookId(bookId));
                    return book;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding book by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BOOKS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Book book = mapResultSetToBook(resultSet);
                book.setAuthors(findAuthorsByBookId(book.getBookId()));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all books: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public List<Book> findByStatus(String status) {
        List<Book> books = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOKS_BY_STATUS)) {
            
            statement.setString(1, status);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    book.setAuthors(findAuthorsByBookId(book.getBookId()));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding books by status: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public List<Book> findByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BOOKS_BY_CATEGORY)) {
            
            statement.setInt(1, categoryId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    book.setAuthors(findAuthorsByBookId(book.getBookId()));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding books by category: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_BOOKS)) {
            
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    book.setAuthors(findAuthorsByBookId(book.getBookId()));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public int getTotalBooks() {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BOOKS);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting books: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public int getBooksByStatus(String status) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BOOKS_BY_STATUS)) {
            
            statement.setString(1, status);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting books by status: " + e.getMessage(), e);
        }
        return 0;
    }
    
    private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setBookId(resultSet.getInt("book_id"));
        book.setCategoryId(resultSet.getInt("cat_id"));
        book.setTitle(resultSet.getString("book_title"));
        book.setPages(resultSet.getInt("book_pages"));
        book.setPublicationYear(resultSet.getString("book_pub_year"));
        book.setStatus(resultSet.getString("status"));
        
        // Set category if available
        String categoryName = resultSet.getString("cat_name");
        if (categoryName != null) {
            Category category = new Category(book.getCategoryId(), categoryName);
            book.setCategory(category);
        }
        
        return book;
    }
    
    private List<Author> findAuthorsByBookId(int bookId) {
        String query = "SELECT a.* FROM author a " +
                      "JOIN authoring au ON a.author_id = au.author_id " +
                      "WHERE au.book_id = ?";
        
        List<Author> authors = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Author author = new Author();
                    author.setAuthorId(resultSet.getInt("author_id"));
                    author.setAuthorName(resultSet.getString("author_name"));
                    authors.add(author);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding authors by book ID: " + e.getMessage(), e);
        }
        
        return authors;
    }
    
    private void saveBookAuthors(Connection connection, Book book) throws SQLException {
        String insertAuthoring = "INSERT INTO authoring (author_id, book_id, contribution_type) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(insertAuthoring)) {
            for (Author author : book.getAuthors()) {
                statement.setInt(1, author.getAuthorId());
                statement.setInt(2, book.getBookId());
                statement.setString(3, "AUTHOR"); // Default contribution type
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    
    private void deleteBookAuthors(Connection connection, int bookId) throws SQLException {
        String deleteAuthoring = "DELETE FROM authoring WHERE book_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(deleteAuthoring)) {
            statement.setInt(1, bookId);
            statement.executeUpdate();
        }
    }

public List<Book> findByStatusWithAuthors(String status) {
    List<Book> books = new ArrayList<>();
    String query = 
        "SELECT b.*, c.cat_name, c.cat_id, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as authors " +
        "FROM books b " +
        "LEFT JOIN categories c ON b.cat_id = c.cat_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE b.status = ? " +
        "GROUP BY b.book_id " +
        "ORDER BY b.book_title";
    
    try (Connection connection = DatabaseUtil.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        
        statement.setString(1, status);
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Book book = mapResultSetToBook(resultSet);
                
                // // Fix 1: Convert authors string to List<Author>
                // String authorsString = resultSet.getString("authors");
                // List<Author> authorsList = parseAuthorsString(authorsString);
                // book.setAuthors(authorsList);
                
                //Create Category object from string and ID
                String categoryName = resultSet.getString("cat_name");
                int categoryId = resultSet.getInt("cat_id");
                if (categoryName != null) {
                    Category category = new Category();
                    category.setCatId(categoryId);
                    category.setCatName(categoryName);
                    book.setCategory(category);
                }
                
                books.add(book);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error finding books by status with authors: " + e.getMessage(), e);
    }
    
    return books;
}


// Helper method to convert comma-separated author names to List<Author>
private List<Author> parseAuthorsString(String authorsString) {
    List<Author> authors = new ArrayList<>();
    
    if (authorsString != null && !authorsString.trim().isEmpty()) {
        String[] authorNames = authorsString.split(", ");
        for (String authorName : authorNames) {
            Author author = new Author();
            author.setAuthorName(authorName.trim());
            authors.add(author);
        }
    }
    
    return authors;
}

}
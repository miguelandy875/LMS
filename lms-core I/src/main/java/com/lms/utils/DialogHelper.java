public class DialogHelper {

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    public static Dialog<Book> createBookDialog(Book book, List<Category> categories, List<Author> authors) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(book == null ? "Add Book" : "Edit Book");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleField = new TextField(book != null ? book.getTitle() : "");

        ComboBox<Category> categoryCombo = new ComboBox<>(FXCollections.observableArrayList(categories));
        if (book != null) categoryCombo.setValue(book.getCategory());

        ComboBox<Author> authorCombo = new ComboBox<>(FXCollections.observableArrayList(authors));
        if (book != null) authorCombo.setValue(book.getAuthor());

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryCombo, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorCombo, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (titleField.getText().isEmpty() || categoryCombo.getValue() == null || authorCombo.getValue() == null) {
                    showError("All fields are required.");
                    return null;
                }

                Book newBook = new Book();
                if (book != null) newBook.setBookId(book.getBookId());

                newBook.setTitle(titleField.getText());
                newBook.setCategory(categoryCombo.getValue());
                newBook.setAuthor(authorCombo.getValue()); // Or Authoring association
                newBook.setUserStatus(true); // or default
                newBook.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                return newBook;
            }
            return null;
        });

        return dialog;
    }
}

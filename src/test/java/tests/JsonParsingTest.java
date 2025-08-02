package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.model.BookWrapper;
import tests.model.Book;

import java.io.InputStream;
import java.time.LocalDate;


public class JsonParsingTest {

    private final ClassLoader cl = JsonParsingTest.class.getClassLoader();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @DisplayName("Разбор json файла библиотекой Jackson")
    @Test
    void parseJsonWithJackson() throws Exception {
        try (InputStream jsonString = cl.getResourceAsStream("Book.json")) {
            Assertions.assertNotNull(jsonString, "Файл <<Book.json>> не найден в resources");

            BookWrapper wrapper = mapper.readValue(jsonString, BookWrapper.class);

            Book book = wrapper.getBook();

            Assertions.assertEquals("Гарри Поттер и философский камень", book.getTitle());
            Assertions.assertEquals("Джоан Роулинг", book.getAuthor());
            Assertions.assertEquals(1997, book.getPublishedYear());
            Assertions.assertEquals("978-5-389-07435-4", book.getIsbn());
            Assertions.assertEquals(3, book.getGenres().size());
            Assertions.assertEquals("Фэнтези", book.getGenres().get(0));
            Assertions.assertEquals(432, book.getPageCount());
            Assertions.assertEquals("Махаон", book.getPublisher());
            Assertions.assertEquals("Русский", book.getLanguage());
            Assertions.assertTrue(book.isAvailable());
            Assertions.assertEquals(2, book.getBorrowHistory().size());
            Assertions.assertEquals("LIB-02315", book.getBorrowHistory().get(0).getUserId());
            Assertions.assertEquals(LocalDate.of(2023, 5, 10), book.getBorrowHistory().get(0).getBorrowDate());
            Assertions.assertEquals(LocalDate.parse("2023-06-01"), book.getBorrowHistory().get(0).getReturnDate());
            Assertions.assertEquals(4, book.getRatings().size());
            Assertions.assertEquals(5.0, book.getRatings().get(1));
        }
    }
}

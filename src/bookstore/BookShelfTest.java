package bookstore;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BookShelfTest {
	private BookShelf bookShelf;
	private static List<Book> bookList;

	@BeforeAll
	static void beforeAll() {
		Book effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2019, Month.MAY, 8));
		Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2019, Month.JUNE, 9));
		Book mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks",
				LocalDate.of(2019, Month.JANUARY, 2));
		Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2019, Month.AUGUST, 1));
		bookList = new ArrayList<Book>();
		bookList.add(effectiveJava);
		bookList.add(codeComplete);
		bookList.add(cleanCode);
		bookList.add(mythicalManMonth);
	}

	@DisplayName("Testing Method add")
	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class MethodTestAddBook {
		@BeforeEach
		void beforeEach() {
			bookShelf = new BookShelf();
			bookShelf.add(bookList);
		}

		@DisplayName("Testing Method add for empty book")
		@ParameterizedTest
		@MethodSource("generateDataForEmptyBookList")
		void testShouldReturnEmptyBook(List<Book> books) {
			int size = bookShelf.books().size();
			bookShelf.add(books);
			assertEquals(size, bookShelf.books().size());
		}

		@DisplayName("Testing Method add for non empty book")
		@ParameterizedTest
		@MethodSource("generateDataForBookList")
		void testShouldReturnAddedBook(List<Book> books) {
			int size = bookShelf.books().size();
			bookShelf.add(books);
			assertEquals(size + books.size(), bookShelf.books().size());
		}

		@DisplayName("Testing Method when throws exception")
		@ParameterizedTest
		@MethodSource("generateDataForBookList")
		void testShouldThrowException(List<Book> books) {
			bookShelf = new BookShelf(0);
			BookShelfCapacityReached exception = assertThrows(BookShelfCapacityReached.class, () -> {
				bookShelf.add(bookList);
			});
			assertEquals("BookShelf capacity of 0 is reached. You can't add more books.", exception.getMessage());
		}

		private Stream<Arguments> generateDataForBookList() {
			List<Book> books = new ArrayList<>();
			books.add(bookList.get(0));
			return Stream.of(Arguments.of(books));
		}

		private Stream<Arguments> generateDataForEmptyBookList() {
			List<Book> books = new ArrayList<>();
			return Stream.of(Arguments.of(books));
		}
	}

	@DisplayName("Testing Method progress")
	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class MethodTestProgress {
		@BeforeEach
		void beforeEach() {
			bookShelf = new BookShelf();
			bookShelf.add(bookList);
		}


		@DisplayName("Testing Method progress when progress has started")
		@Test
		void testProgressStarted() {
			Progress progress = bookShelf.progress();
			assertEquals(0, progress.completed());
			assertEquals(0, progress.inProgress());
			assertEquals(100, progress.toRead());
		}

		@DisplayName("Testing Method progress when progress has not started")
		@Test
		void testProgressNotStarted() {
			bookShelf = new BookShelf();
			Progress progress = bookShelf.progress();
			assertEquals(0, progress.completed());
			assertEquals(0, progress.inProgress());
			assertEquals(0, progress.toRead());
		}
	}

	@DisplayName("Testing Method findBooksByTitleOrPublishedDateRange")
	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class MethodTestFindBooks {
		@BeforeEach
		void init() {
			bookShelf = new BookShelf();
			bookShelf.add(bookList);
		}

		@DisplayName("Testing Method findBooksByTitleOrPublishedDateRange for Empty Book List")
		@ParameterizedTest
		@MethodSource("generateDataForEmptyList")
		void testFindShouldReturnEmptyBookList(String title, LocalDate firstDate, LocalDate endDate,
											   List<Book> expected) {
			assertIterableEquals(expected, bookShelf.findBooksByTitleOrPublishedDateRange(title, firstDate, endDate));
		}

		@DisplayName("Testing Method findBooksByTitleOrPublishedDateRange when Published Date is within range")
		@ParameterizedTest
		@MethodSource("generateDataForIfPass")
		void testShouldReturnBooksBetweenDates(String title, LocalDate firstDate, LocalDate endDate,
											   List<Book> expected) {
			assertIterableEquals(expected, bookShelf.findBooksByTitleOrPublishedDateRange(title, firstDate, endDate));
		}

		@DisplayName("Testing Method findBooksByTitleOrPublishedDateRange for matched title")
		@ParameterizedTest
		@MethodSource("generateDataForIfElsePass")
		void testShouldReturnBooksMatchTitle(String title, LocalDate firstDate, LocalDate endDate,
											 List<Book> expected) {
			assertIterableEquals(expected, bookShelf.findBooksByTitleOrPublishedDateRange(title, firstDate, endDate));
		}

		@DisplayName("Testing Method findBooksByTitleOrPublishedDateRange when Published Date is not within range " +
				"title is not matched and")
		@ParameterizedTest
		@MethodSource("generateDataForIfElseNotPass")
		void testShouldReturnNoBookFoundByDateAndTitle(String title, LocalDate firstDate, LocalDate endDate,
													   List<Book> expected) {
			assertIterableEquals(expected, bookShelf.findBooksByTitleOrPublishedDateRange(title, firstDate, endDate));
		}

		private Stream<Arguments> generateDataForIfElsePass() {
			return Stream.of(Arguments.of("Effective Java", LocalDate.of(2019, Month.SEPTEMBER, 1),
					LocalDate.of(2019, Month.DECEMBER, 30), new ArrayList<Book>() {
						{
							add(bookList.get(0));
						}
					})

			);
		}

		private Stream<Arguments> generateDataForIfElseNotPass() {
			return Stream.of(Arguments.of("Effective Python", LocalDate.of(2019, Month.SEPTEMBER, 1),
					LocalDate.of(2019, Month.DECEMBER, 30), new ArrayList<Book>() {
						{
						}
					})

			);
		}

		private Stream<Arguments> generateDataForEmptyList() {
			return Stream
					.of(Arguments.of("test", LocalDate.of(2020, 1, 2), LocalDate.of(2020, 1, 3), new ArrayList<Book>())

					);
		}

		private Stream<Arguments> generateDataForIfPass() {
			return Stream.of(Arguments.of("test", LocalDate.of(2019, Month.APRIL, 1),
					LocalDate.of(2019, Month.JULY, 30), new ArrayList<Book>() {
						{
							add(bookList.get(0));
							add(bookList.get(1));
						}
					}), Arguments.of("test", LocalDate.of(2019, Month.JANUARY, 1),
					LocalDate.of(2019, Month.DECEMBER, 30), bookList)

			);
		}


	}







}

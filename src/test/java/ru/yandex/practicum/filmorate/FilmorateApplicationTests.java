package ru.yandex.practicum.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ UserDbStorage.class })
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;

	@BeforeEach
	void setUp() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testuser");
		user.setName("Test User");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user);
	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = userStorage.findUserById(1);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1));
	}
}

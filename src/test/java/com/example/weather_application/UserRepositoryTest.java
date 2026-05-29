package com.example.weather_application;

import com.example.weather_application.repos.UserRepository;
import com.example.weather_application.entities.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("поиск юзера по userId - успешно")
    public void findByUsername_UserExists() {
        String username = "Tutta";
        UserEntity userEntity = new UserEntity(
                null,
                "Tutta"
        );

        entityManager.persistAndFlush(userEntity);

        Optional<UserEntity> found = userRepository.findByUserId(username);

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Tutta", found.get().getUserId());
    }

    @Test
    @DisplayName("поиск юзера по userId, юзера не существует")
    public void findByUsername_UserNotExists() {
        Optional<UserEntity> found = userRepository.findByUserId("username");

        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("сохранение юзера, автогенерация Id")
    public void saveUser_AutoGenerateId() {
        UserEntity userEntity = new UserEntity(
                null,
                "Tutta"
        );

        UserEntity savedUser = userRepository.save(userEntity);

        Assertions.assertNotNull(userEntity.getId());
        Assertions.assertEquals("Tutta", savedUser.getUserId());
    }

    @Test
    @DisplayName("удалить юзера по Id")
    public void deleteUserById_UserExists() {
        UserEntity userEntity = new UserEntity(
                null,
                "Tutta"
        );

        Long id = entityManager.persistAndFlush(userEntity).getId();

        userRepository.deleteById(id);
        Optional<UserEntity> found = userRepository.findById(id);

        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("поиск пользователя по email - пользователь существует")
    public void findByEmail_EmailExists() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("tutta@test.com");
        userEntity.setId(null);
        userEntity.setUserId("Tutta");

        String email = entityManager.persistAndFlush(userEntity).getEmail();

        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("tutta@test.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("поиск пользователя по email - пользователь не существует")
    public void findByEmail_EmailNotExists() {
        String email = "tutta@test.com";

        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        Assertions.assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("проверка на существование пользователя по email - существует")
    public void existsByEmail_EmailExists() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("tutta@test.com");
        userEntity.setId(null);
        userEntity.setUserId("Tutta");

        String email = entityManager.persistAndFlush(userEntity).getEmail();

        boolean exists = userRepository.existsByEmail(email);

        Assertions.assertTrue(exists);
    }

    @Test
    @DisplayName("проверка на существование пользователя по email - не существует")
    public void existsByEmail_EmailNotExists() {
        String email = "tutta@test.com";

        boolean exists = userRepository.existsByEmail(email);

        Assertions.assertFalse(exists);
    }

}

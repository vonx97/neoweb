package com.neosoft.neoweb.Services;

import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.UserRepository;
import com.neosoft.neoweb.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("test","secret","adam","sandler", LocalDateTime.now());
        user.setId(1);
        user.setEmail("test@example.com");
    }

    // --- addUser test ---
    @Test
    void shouldThrowException_whenUsernameExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    // --- updateUser tests ---
    @Test
    void shouldUpdateUser_whenUserExists() {
        User updatedUser = new User("updated","secret","Updated","sandler", LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1, updatedUser);

        assertThat(result.getUsername()).isEqualTo("updated");
        assertThat(result.getName()).isEqualTo("Updated");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        User updatedUser = new User("","","","",LocalDateTime.now()); // geçerli bir User objesi

        assertThatThrownBy(() -> userService.updateUser(1, updatedUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // --- deleteUser tests ---
    @Test
    void shouldDeleteUser_whenUserExists() {
        when(userRepository.existsById(1)).thenReturn(true);

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistingUser() {
        when(userRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}

package com.neosoft.neoweb.services;

import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(int id, User updatedUser) {

        return userRepository.findById(id).map(user -> {

            user = new User(updatedUser.getUsername(),updatedUser.getPassword(),updatedUser.getName(),updatedUser.getSurname(),updatedUser.getCreationDate());

            user.setEmail(updatedUser.getEmail());
            user.setPhone(updatedUser.getPhone());
            user.setAddress(updatedUser.getAddress());
            user.setCity(updatedUser.getCity());
            user.setCountry(updatedUser.getCountry());
            user.setDistrict(updatedUser.getDistrict());
            user.setIdentityNumber(updatedUser.getIdentityNumber());
            user.setRoles(updatedUser.getRoles());

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));

    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }




}

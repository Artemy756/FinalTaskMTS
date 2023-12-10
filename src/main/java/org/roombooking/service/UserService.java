package org.roombooking.service;

import org.roombooking.entity.User;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.UserRepository;
import org.roombooking.service.exceptions.UserCreateException;
import org.roombooking.service.exceptions.UserNotFoundException;

import java.util.List;

public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getAllUser() {
    return userRepository.getAllUsers();
  }

  public User getUserById(UserId id) {
    try {
      return userRepository.getUserById(id);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("Cannot find user with id: " + id, e);
    }
  }

  public User getUserByPhoneNumber(String phoneNumber) {
    try {
      return userRepository.getUserByPhoneNumber(phoneNumber);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("Cannot find user with phone number: " + phoneNumber, e);
    }
  }

  public User getUserByEmail(String email) {
    try {
      return userRepository.getUserByEmail(email);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("Cannot find user with email: " + email, e);
    }
  }

  public UserId addUser(String name, String phoneNumber, String email) {
    UserId userId = userRepository.generateId();
    User user = new User(userId, name, phoneNumber, email);
    try {
      userRepository.addUser(user);
    } catch (RuntimeException e) {
      throw new UserCreateException("Cannot create user", e);
    }
    return userId;
  }

}

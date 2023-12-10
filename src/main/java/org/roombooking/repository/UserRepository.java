package org.roombooking.repository;

import org.roombooking.entity.User;
import org.roombooking.entity.id.UserId;

import java.util.List;


public interface UserRepository {
  UserId generateId();
  void addUser(User user);
  List<User> getAllUsers();
  User getUserById(UserId userId);
  User getUserByPhoneNumber(String phoneNumber);
  User getUserByEmail(String email);

}

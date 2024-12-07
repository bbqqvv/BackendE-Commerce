package org.bbqqvv.backendecommerce.service;



import org.bbqqvv.backendecommerce.entity.User;

import java.util.List;

public interface UserService {
	User createUser (User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser (Long id, User user);
    void deleteUser (Long id);
	User getUserByUsername(String username);
    boolean existsByUsername(String username);
}

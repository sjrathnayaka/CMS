package edu.epic.cms.repository;

import edu.epic.cms.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 */
public interface UserRepo {

    User save(User user);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean existsByUsername(String username);
}

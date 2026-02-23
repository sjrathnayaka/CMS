package edu.epic.cms.repository;

import edu.epic.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for User entity.
 */
public interface UserRepository extends JpaRepository<User, String> {
}

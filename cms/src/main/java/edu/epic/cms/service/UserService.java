package edu.epic.cms.service;

import edu.epic.cms.model.User;
import java.util.List;

/**
 * Service interface for user-related operations.
 */
public interface UserService {

    /**
     * Get all users.
     */
    List<User> getAllUsers();
}

package edu.epic.cms.service.impl;

import edu.epic.cms.model.User;
import edu.epic.cms.repository.UserRepository;
import edu.epic.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of UserService.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching users from the database", e);
            throw e;
        }
    }
}

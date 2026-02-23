package edu.epic.cms.repository.impl;

import edu.epic.cms.model.User;
import edu.epic.cms.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserRepo.
 */
@Repository
@RequiredArgsConstructor
public class UserRepoImpl implements UserRepo {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> new User(
            rs.getString("Username"),
            rs.getString("Name"),
            rs.getString("Status"));

    @Override
    public User save(User user) {
        String sql = "INSERT INTO Users (Username, Name, Status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getName(), user.getStatus());
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, rowMapper, username);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users ORDER BY Username";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}

package edu.epic.cms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a system user.
 * Maps to the Users table in the database.
 * Status references RequestStatus(StatusCode): PEND, APPR, RJCT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    private String username ; // Primary Key

    private String name;

    private String status; // FK → RequestStatus(StatusCode): PEND, APPR, RJCT
}

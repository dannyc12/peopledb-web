package com.danworld.peopledbweb.business.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

//@Data allows Lombok library to scan this class, find these fields,
// and dynamically generate getters and setters (and constructors for us)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="people", schema="public")
public class Person {
    @Id
    @GeneratedValue //tells hibernate/Spring to generate the id from the db
    private Integer id;

    //these annotation are form entry validators
    @NotEmpty(message="First name can not be empty")
    private String firstName;
    @NotEmpty(message="Last name can not be empty")
    private String lastName;
    @Past(message="Date of birth must be in the past")
    @NotNull(message="Date of birth must be specified")
    private LocalDate dob;
    @NotEmpty(message="Please enter an email address")
    @Email(message="Email must be valid")
    private String email;
    @DecimalMin(value="1000.00", message = "Salary must be at least 1000.00")
    @NotNull(message = "Salary can not be empty")
    private BigDecimal salary;

    private String photoFileName;
}

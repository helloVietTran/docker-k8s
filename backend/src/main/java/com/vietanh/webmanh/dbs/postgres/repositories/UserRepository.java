package com.vietanh.webmanh.dbs.postgres.repositories;

import java.util.Optional;

import com.vietanh.webmanh.dbs.postgres.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
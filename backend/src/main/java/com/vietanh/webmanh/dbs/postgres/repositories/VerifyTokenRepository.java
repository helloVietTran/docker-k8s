package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.VerifyAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VerifyTokenRepository extends JpaRepository<VerifyAccountToken, String> {
}

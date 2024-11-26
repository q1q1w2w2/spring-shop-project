package com.example.demo1.repository.login;

import com.example.demo1.domain.login.RefreshToken;
import com.example.demo1.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String RefreshToken);

    @Query("select r from RefreshToken r where r.user = :user and r.expiredAt is null")
    Optional<List<RefreshToken>> findNonExpiredRefreshTokensByUser(@Param("user") User user);
}

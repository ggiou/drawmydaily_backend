package com.example.mypost.repository;

import com.example.mypost.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepositroy extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String key);
}

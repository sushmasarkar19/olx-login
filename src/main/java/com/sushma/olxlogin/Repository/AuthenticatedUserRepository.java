package com.sushma.olxlogin.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sushma.olxlogin.entity.AuthenticatedUserEntity;

@Repository
public interface AuthenticatedUserRepository extends JpaRepository<AuthenticatedUserEntity, UUID>{

}

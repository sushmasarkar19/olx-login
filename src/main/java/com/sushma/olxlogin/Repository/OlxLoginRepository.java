package com.sushma.olxlogin.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sushma.olxlogin.entity.UserEntity;

@Repository
public interface OlxLoginRepository extends JpaRepository<UserEntity, Integer>{
	
	Optional<UserEntity> findByUserName(String userName);

}

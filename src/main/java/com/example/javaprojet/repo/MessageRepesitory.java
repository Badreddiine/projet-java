package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author $ {USERS}
 **/
public interface MessageRepesitory extends JpaRepository<Message, Long> {
}

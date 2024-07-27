package com.gettimhired.model.mongo;

import org.springframework.data.annotation.Id;

import java.util.List;

public record User(
        @Id String id,
        String password,
        String email,
        String emailPassword,
        List<String> roles
) {
}

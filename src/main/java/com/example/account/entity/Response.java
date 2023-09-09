package com.example.account.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Response {
    private int status;
    private Object data;
    private String message;
}

package com.example.account.domain.logs.entity;

import com.example.account.util.enums.EventType;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventLog {
    private UUID id;
    private EventType eventType;
    private UUID objectId;
}

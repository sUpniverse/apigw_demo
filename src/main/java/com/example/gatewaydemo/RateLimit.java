package com.example.gatewaydemo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ratelimit")
@Getter
@Setter
@NoArgsConstructor
public class RateLimit {

    @Id
    @Column(name = "client_id")
    String clientId;

    @Column(name = "value")
    int value;
}

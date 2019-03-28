package com.example.account.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    private String name;

//    private

}

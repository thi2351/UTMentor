package com.example.utmentor.models.webModels.users;
import java.util.List;

public record UserRespone(String username,
                          String firstName,
                          String lastName,
                          List<String> role,
                          String avatarUrl
) {}

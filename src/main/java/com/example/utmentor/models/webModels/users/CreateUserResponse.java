package com.example.utmentor.models.webModels.users;

public class CreateUserResponse {
    private String msg;
    public CreateUserResponse(String usr) {
        msg = "Chào mừng " + usr + " đến với bình nguyên vô tận";
        System.out.println("Chào mừng " + usr + " đến với bình nguyên vô tận");
    }

    public String getMsg() {
        return msg;
    }
}

// Viewへの入出力時に使用するJavaBeansのような入れ物
package com.example.forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentForm {

    private int id;
    @NotBlank(message = "コメントを入力してください")
    private String text;
    private int messageId;
    private Date createdDate;
    private Date updatedDate;
}

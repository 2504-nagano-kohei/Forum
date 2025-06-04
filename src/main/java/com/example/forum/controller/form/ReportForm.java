// Viewへの入出力時に使用するJavaBeansのような入れ物
package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ReportForm {

    @NotBlank(message = "投稿内容を入力してください")
    private String content;

    private int id;
    private Date createdDate;
    private Date updatedDate;
}

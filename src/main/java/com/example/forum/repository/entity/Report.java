// データアクセス時に使用するJavaBeansのような入れ物
package com.example.forum.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {
    @Id

    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @Column(name = "created_date", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    // ★課題：5. 表⽰順をコメントも含めて、降順に変更→フィールド追加
    @Column(name = "updated_date", insertable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
}

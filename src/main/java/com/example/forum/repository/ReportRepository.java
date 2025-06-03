package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
// ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、
// こちらで特に何か記載する必要はありません。
// findAllで実行されている処理はSQL文の「select * from report;」ようなイメージ
public interface ReportRepository extends JpaRepository<Report, Integer> {
    // ★課題：5. 表⽰順をコメントも含めて、更新日時の降順に変更
    List<Report> findAllByOrderByUpdatedDateDesc();
    public List<Report> findByCreatedDateBetween(Date start, Date end);
}

package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     * reportRepositoryのfindAllを実行
     */
    public List<ReportForm> findAllReport(String startDate, String endDate) throws ParseException {
        List<Report> results = new ArrayList<>();
        // 現在日時を取得&表示形式を指定
        Date nowDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dafaultStartDate = "2020-01-01 00:00:00";
        String dafaultEndDate = sdFormat.format(nowDate);

        // どっちも未入力の場合はIDによる全件取得
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            // ★課題：5. 表⽰順をコメントも含めて、降順に変更
            results = reportRepository.findAllByOrderByUpdatedDateDesc();
        // そうでない場合（どっちも入力ありorどっちか入力あり）は日付指定で絞り込み取得
        } else {
            // ※入力の有無を判定し、開始と終了の日時を引数に設定
            if (!StringUtils.isBlank(startDate)) {
                startDate += " 00:00:00";
            } else {
                startDate = dafaultStartDate;
            }
            if (!StringUtils.isBlank(endDate)) {
                endDate += " 23:59:59";
            } else {
                endDate = dafaultEndDate;
            }

            Date start = sdFormat.parse(startDate);
            Date end = sdFormat.parse(endDate);
            results = reportRepository.findByCreatedDateBetween(start, end);
        }
        List<ReportForm> reports = setReportForm(results);
        return reports;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        // リストを用意
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            // Entityから
            Report result = results.get(i);
            // Formへ
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            // ★課題：5. 表⽰順をコメントも含めて、降順に変更→updatedDateもsetに追加
            report.setUpdatedDate(result.getUpdatedDate());
            // 用意したリストへ詰める
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) throws ParseException {
        // setReportEntityメソッドでFormからEntityに詰め直してRepositoryに渡している
        Report saveReport = setReportEntity(reqReport);
        // saveメソッド→テーブルに新規投稿をinsertするような処理になっている。その他にもupdate文のような処理も兼ね備えている
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    Report setReportEntity(ReportForm reqReport) throws ParseException {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setCreatedDate(reqReport.getCreatedDate());

        // 現在日時を取得
        Date nowDate = new Date();
        // フォーマットを指定
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uDate = sdFormat.format(nowDate);
        Date updatedDate = sdFormat.parse(uDate);
        report.setUpdatedDate(updatedDate);
        return report;
    }

    /*
     * レコード削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    /*
     * レコード1件取得
     */
    public ReportForm editReport(Integer id) {
        List<Report> results = new ArrayList<>();
        results.add((Report) reportRepository.findById(id).orElse(null));
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }
}

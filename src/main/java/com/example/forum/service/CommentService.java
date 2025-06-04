package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     * commentRepositoryのfindAllを実行
     */
    public List<CommentForm> findAllComment() {
        List<Comment> results = commentRepository.findAllByOrderByUpdatedDateDesc();
        List<CommentForm> comments = setCommentForm(results);
        return comments;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setText(result.getText());
            comment.setMessageId(result.getMessageId());
            comments.add(comment);
        }
        return comments;
    }


    /*
     * コメント追加
     */
    public void saveComment(CommentForm commentForm, ReportForm reportForm) throws ParseException {
        // setCommentEntityメソッドでFormからEntityに詰め直してRepositoryに渡している
        Comment saveComment = setCommentEntity(commentForm);

        // コメントを更新したら、投稿のupdated_dateも更新
        reportForm.setId(commentForm.getMessageId());
        if(reportForm.getContent() != null) {
            Report saveReport = new ReportService().setReportEntity(reportForm);
            reportRepository.save(saveReport);
        }
        // saveメソッドはテーブルに新規投稿をinsertするような処理になっている。その他にも、update文のような処理も兼ね備えている
        commentRepository.save(saveComment);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comment setCommentEntity(CommentForm commentForm) throws ParseException {
        // Entityを生成(ReportのEntityも生成？)
        Comment comment = new Comment();
        comment.setId(commentForm.getId());
        comment.setText(commentForm.getText());
        comment.setMessageId(commentForm.getMessageId());
        comment.setCreatedDate(commentForm.getCreatedDate());

        // 現在日時を取得してEntityに詰めてあげる
        Date nowDate = new Date();
        // フォーマットを指定
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uDate = sdFormat.format(nowDate);
        Date updatedDate = sdFormat.parse(uDate);
        comment.setUpdatedDate(updatedDate);
        return comment;
    }

    /*
     * レコード1件取得 ★課題：コメント編集機能追加
     */
    public CommentForm editComment(Integer id) {
        List<Comment> results = new ArrayList<>();
        // CommentRepository は JpaRepository を継承しているため、findById() メソッドを使います。
        // Id が一致するレコードを取得するような処理。Id が合致しないときは null を返したいので、.orElse(null)
        results.add((Comment) commentRepository.findById(id).orElse(null));
        List<CommentForm> comments = setCommentForm(results);
        return comments.get(0);
    }

    /*
     * コメントレコード削除<!-- コメント編集機能追加
     */
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}

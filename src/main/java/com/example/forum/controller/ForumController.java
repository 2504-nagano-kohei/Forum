package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Report;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    // コメント機能実装課題で追加
    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name="startDate", required=false) String startDate,
                            @RequestParam(name="endDate", required=false) String endDate) throws ParseException  {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport(startDate, endDate);
        // コメントを全件取得
        List<CommentForm> commentData = commentService.findAllComment();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        // コメントデータオブジェクトを保管
        mav.addObject("comments", commentData);
        mav.addObject("commentForm", new CommentForm());
        return mav;
    }

    /*
     * コメント投稿処理
     */
    @PostMapping("/addComment")
    public ModelAndView addComment(@Validated @ModelAttribute("commentForm") CommentForm commentForm,
                                   BindingResult result,
                                   @RequestParam(name="startDate", required=false) String startDate,
                                   @RequestParam(name="endDate", required=false) String endDate,
                                   ReportForm reportForm) throws ParseException {

        ModelAndView mav = new ModelAndView();
        // バリデーション
        if(result.hasErrors()) {

            mav.setViewName("/top");
            mav.addObject("commentForm", commentForm); // 入力内容を保持

            // 投稿を全件取得
            List<ReportForm> contentData = reportService.findAllReport(startDate, endDate);
            // コメントを全件取得
            List<CommentForm> commentData = commentService.findAllComment();
            // 投稿データオブジェクトを保管
            mav.addObject("contents", contentData);
            // コメントデータオブジェクトを保管
            mav.addObject("comments", commentData);

            return mav;
        }
        // コメントをテーブルに格納
        commentService.saveComment(commentForm, reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@Validated @ModelAttribute("formModel") ReportForm reportForm, BindingResult
            result) throws ParseException {
        if(result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/new");
//            for(FieldError error : result.getFieldErrors()) {
//                String field = error.getField();
//                String Message = error.getDefaultMessage();
//            }
            mav.addObject("formModel", reportForm); // 入力内容を保持
            return mav;
        }

        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    // 「@PathVariable」は form タグ内の action 属性に記述されている { } 内で指定されたURLパラメータを取得する
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集処理(編集したい投稿のIDに該当する投稿を取得し、編集画面に遷移)
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent (@PathVariable Integer id,
                                       @Validated @ModelAttribute("formModel") ReportForm reportForm,
                                       BindingResult result) throws ParseException {
        if(result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/new");
//            for(FieldError error : result.getFieldErrors()) {
//                String field = error.getField();
//                String Message = error.getDefaultMessage();
//            }
            mav.addObject("formModel", reportForm); // 入力内容を保持
            return mav;
        }
        // UrlParameterのidを更新するentityにセット
        reportForm.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }




    /*
     * コメント編集画面の表示処理　★課題：コメント編集機能追加
     */
    @GetMapping("/editComment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        CommentForm comment = commentService.editComment(id);
        // 編集する投稿をセット
        mav.addObject("formModel", comment);
        mav.addObject("commentForm", new CommentForm());
        // 画面遷移先を指定
        mav.setViewName("/editComment");
        return mav;
    }

    /*
     * コメント編集処理 ★課題：コメント編集機能追加
     */
    // 編集画面から、id および formModel の変数名で入力された投稿内容を受け取る
    @PutMapping("/updateComment/{id}")
    public ModelAndView updateComment (@PathVariable Integer id,
                                       @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult result,
                                       ReportForm report) throws ParseException {
        ModelAndView mav = new ModelAndView();
        // バリデーション
        if(result.hasErrors()) {

            mav.setViewName("/editComment");
            mav.addObject("commentForm", commentForm); // 入力内容を保持

            // 編集する投稿を取得
            CommentForm comment = commentService.editComment(id);
            // 編集する投稿をセット
            mav.addObject("formModel", comment);

            return mav;
        }
        // 「comment.setId(id);」で、指定されたidを　UrlParameterのidを更新するentityにセット→saveComment メソッドへ行って、投稿の更新処理を行う
        commentForm.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(commentForm, report);
        // 更新処理が終わったら、top画面へ遷移して、最新の状態を表示したいので、投稿内容表示画面(root)へリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント削除処理　★課題：コメント削除機能追加
     */
    @DeleteMapping("/deleteComment/{id}")
    // 「@PathVariable」は form タグ内の action 属性に記述されている { } 内で指定されたURLパラメータを取得する
    public ModelAndView deleteComment(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}

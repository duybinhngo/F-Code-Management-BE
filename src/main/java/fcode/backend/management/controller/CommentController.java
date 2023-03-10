package fcode.backend.management.controller;

import fcode.backend.management.model.dto.CommentDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping
    public Response<Void> createComment(@RequestBody CommentDTO commentDTO, @RequestAttribute(required = false) String userEmail) {
        return commentService.createComment(commentDTO, userEmail);
    }

    @GetMapping("/{commentId}")
    public Response<CommentDTO> getCommentById(@PathVariable Integer commentId) {
        return commentService.getCommentById(commentId);
    }
    @GetMapping("/question/{questionId}")
    public Response <List<CommentDTO>> getAllCommentsOfAQuestion(@PathVariable Integer questionId) {
        return commentService.getAllCommentsOfAQuestion(questionId);
    }
    @GetMapping("/question/latest/{questionId}")
    public Response<List<CommentDTO>> getLatestCommentsOfAQuestion(@PathVariable Integer questionId) {
        return commentService.getLatestCommentsOfAQuestion(questionId);
    }
    @GetMapping("/latest")
    public Response<List<CommentDTO>> getLatestComments() {
        return commentService.getLatestComments();
    }
    @PutMapping
    public Response<Void> updateComment(@RequestBody CommentDTO commentDTO, @RequestAttribute(required = false) String userEmail) {
        return commentService.updateContent(commentDTO, userEmail);
    }

    @DeleteMapping("/{commentId}")
    public Response<Void> deleteCommentById(@PathVariable Integer commentId, @RequestAttribute(required = false) String userEmail) {
        return commentService.deleteComment(commentId, userEmail);
    }

    @DeleteMapping("/author")
    public Response<Void> deleteCommentByAuthor(@RequestParam String authorEmail) {
        return commentService.deleteAllCommentByAuthorEmail(authorEmail);
    }
}

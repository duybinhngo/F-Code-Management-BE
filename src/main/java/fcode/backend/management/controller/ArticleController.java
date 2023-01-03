package fcode.backend.management.controller;

import fcode.backend.management.model.dto.ArticleDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @PostMapping
    Response<Void> createArticle(@RequestBody ArticleDTO articleDTO, @RequestAttribute(required = false) String userEmail) {
        return articleService.createArticle(articleDTO, userEmail);
    }

    @GetMapping("/{id}")
    Response<ArticleDTO> getArticleById(@PathVariable Integer id) {
        return articleService.getArticleById(id);
    }

    @GetMapping("/processing")
    Response<List<ArticleDTO>> getProcessingArticle() {
        return articleService.getProcessingArticles();
    }

    @GetMapping("/all")
    Response<List<ArticleDTO>> getAllArticle() {
        return articleService.getAllArticles();
    }

    @GetMapping("/inactive")
    Response<List<ArticleDTO>> getInactiveArticle() {
        return articleService.getInactiveArticles();
    }
    @GetMapping("/user")
    Response<List<ArticleDTO>> getArticlesOfUser(@RequestAttribute(required = false) Integer userId) {
        return articleService.getArticlesOfUser(userId);
    }
    @GetMapping("/author")
    Response<List<ArticleDTO>> getArticlesByAuthor(@RequestParam String author) {
        return articleService.getArticlesByAuthor(author);
    }

    @PutMapping("/approve/{id}")
    Response<Void> approveArticle(@PathVariable Integer id) {
        return articleService.approveArticle(id);
    }

    @PutMapping("/disapprove/{id}")
    Response<Void> disapproveArticle(@PathVariable Integer id) {
        return articleService.disapproveArticle(id);
    }

    @PutMapping("/approve/all")
    Response<Void> approveAllArticles() {
        return articleService.approveAll();
    }

    @PutMapping("/disapprove/all")
    Response<Void> disapproveAllArticles() {
        return articleService.disapproveAll();
    }

    @PutMapping
    Response<Void> updateArticle(@RequestBody ArticleDTO articleDTO, @RequestAttribute(required = false) Integer userId) {
        return articleService.updateArticle(articleDTO, userId);
    }

    @DeleteMapping("/{id}")
    Response<Void> deleteArticle(@PathVariable Integer id, @RequestAttribute(required = false) Integer userId) {
        return articleService.deleteArticleById(id, userId);
    }
}

package fcode.backend.management.repository;

import fcode.backend.management.repository.entity.Article;
import fcode.backend.management.service.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findArticleByIdAndStatus(Integer id, Status status);
    List<Article> findArticleByStatus(Status status);
    Article findArticleByIdAndStatusIsNot(Integer id, Status status);
    List<Article> findArticleByMemberIdAndStatus(Integer memberId, Status status);
    List<Article> findArticleByAuthorAndStatus(String author, Status status);
}

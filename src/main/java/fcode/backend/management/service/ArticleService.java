package fcode.backend.management.service;

import fcode.backend.management.config.Role;
import fcode.backend.management.model.dto.ArticleDTO;
import fcode.backend.management.model.dto.GenreDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.repository.AnnouncementRepository;
import fcode.backend.management.repository.ArticleRepository;
import fcode.backend.management.repository.MemberRepository;
import fcode.backend.management.repository.entity.Article;

import fcode.backend.management.repository.GenreRepository;
import fcode.backend.management.repository.entity.Genre;
import fcode.backend.management.repository.entity.Member;

import fcode.backend.management.service.constant.ServiceMessage;
import fcode.backend.management.service.constant.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ArticleService.class);
    private static final String CREATE_ARTICLE_MESSAGE = "Create article: ";
    private static final String GET_GENRE_MESSAGE = "Get genre: ";
    private static final String UPDATE_ARTICLE_MESSAGE = "Update article: ";
    private static final String GET_ARTICLE_MESSAGE = "Get article: ";
    private static final String DELETE_ARTICLE_MESSAGE = "Delete article: ";
    private static final String APPROVE_ARTICLE = "Approve article: ";
    private static final String DISAPPROVE_ARTICLE = "Disapprove article: ";
    @Autowired
    private AnnouncementRepository announcementRepository;

    public Response<Void> createArticle(ArticleDTO articleDTO, Integer memberId) {
        logger.info("{}{}", CREATE_ARTICLE_MESSAGE, articleDTO);
        if (articleDTO == null) {
            logger.warn("{}{}", CREATE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        if (articleDTO.getGenreId() == null || articleDTO.getTitle() == null) {
            logger.warn("{}{}", CREATE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Member member = memberRepository.findMemberById(memberId);
        if (member == null) {
            logger.warn("{}{}", CREATE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Genre genre = genreRepository.findGenreById(articleDTO.getGenreId());
        if (genre == null) {
            logger.warn("{}{}", CREATE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setId(null);
        article.setMember(member);
        article.setStatus(Status.PROCESSING);
        logger.info("{}{}", CREATE_ARTICLE_MESSAGE, article);
        articleRepository.save(article);
        logger.info("Create article successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> approveArticle(Integer id) {
        logger.info("{}{}", APPROVE_ARTICLE, id);
        Article articleEntity = articleRepository.findArticleByIdAndStatus(id, Status.PROCESSING);
        if (articleEntity == null) {
            logger.warn("{}{}", APPROVE_ARTICLE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        articleEntity.setStatus(Status.ACTIVE);
        articleRepository.save(articleEntity);
        logger.info("Approve article successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    @Transactional
    public Response<Void> approveAll() {
        logger.info("Approve all Article");
        List<Article> articleSet = articleRepository.findArticleByStatus(Status.PROCESSING);
        articleSet.forEach(article -> {
            article.setStatus(Status.ACTIVE);
            articleRepository.save(article);
        });
        logger.info("Approve all articles successfully.");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    @Transactional
    public Response<Void> disapproveAll() {
        logger.info("Disapprove all Article");
        List<Article> articleSet = articleRepository.findArticleByStatus(Status.PROCESSING);
        articleSet.forEach(article -> {
            article.setStatus(Status.INACTIVE);
            articleRepository.save(article);
        });
        logger.info("Disapprove all articles successfully.");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> disapproveArticle(Integer id) {
        logger.info("{}{}", DISAPPROVE_ARTICLE, id);
        Article articleEntity = articleRepository.findArticleByIdAndStatus(id, Status.PROCESSING);
        if (articleEntity == null) {
            logger.warn("{}{}", DISAPPROVE_ARTICLE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        articleEntity.setStatus(Status.INACTIVE);
        articleRepository.save(articleEntity);
        logger.info("Disapprove article successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }
    public Response<GenreDTO> getGenreById(Integer id) {
        logger.info("{}{}", GET_GENRE_MESSAGE, id);
        if (id == null) {
            logger.warn("{}{}", GET_GENRE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Genre genre = genreRepository.findGenreById(id);
        if (genre == null) {
            logger.warn("{}{}", GET_GENRE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        logger.info("Get genre successfully");
        GenreDTO genreDTO = modelMapper.map(genre, GenreDTO.class);
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), genreDTO);
    }
    @Transactional
    public Response<List<GenreDTO>> getAllGenres() {
        logger.info("{}{}", GET_GENRE_MESSAGE, "All genres");
        List<GenreDTO> genreDTOS = genreRepository.findAll().stream().map(map -> modelMapper.map(map, GenreDTO.class)).collect(Collectors.toList());
        logger.info("Get all genres successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), genreDTOS);
    }
    @Transactional
    public Response<List<ArticleDTO>> getAllArticles() {
        logger.info("{}{}", GET_ARTICLE_MESSAGE, "All article");
        List<ArticleDTO> articleDTOSet = articleRepository.findArticleByStatus(Status.ACTIVE).stream().map(map -> modelMapper.map(map, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get all articles successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOSet);
    }

    public Response<ArticleDTO> getArticleById(Integer id) {
        logger.info("{}{}", GET_ARTICLE_MESSAGE, id);
        if (id == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Article article = articleRepository.findArticleByIdAndStatus(id, Status.ACTIVE);
        if (article == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        logger.info("Get article successfully.");
        ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTO);
    }

    @Transactional
    public Response<List<ArticleDTO>> getArticlesByGenreId(Integer genreId) {
        logger.info("{}genre id: {}", GET_ARTICLE_MESSAGE, genreId);
        if (genreId == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Genre genre = genreRepository.findGenreById(genreId);
        if (genre == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        List<ArticleDTO> articleDTOS = genre.getArticles().stream().filter(article -> article.getStatus().equals(Status.ACTIVE)).map(article -> modelMapper.map(article, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get articles by genre id successfully.");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOS);
    }
    @Transactional
    public Response<List<ArticleDTO>> getProcessingArticles() {
        logger.info("{}{}", GET_ARTICLE_MESSAGE, "All processing articles");
        List<ArticleDTO> articleDTOSet = articleRepository.findArticleByStatus(Status.PROCESSING).stream().map(map -> modelMapper.map(map, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get all processing articles successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOSet);
    }

    @Transactional
    public Response<List<ArticleDTO>> getInactiveArticles() {
        logger.info("{}{}", GET_ARTICLE_MESSAGE, "All processing articles");
        List<ArticleDTO> articleDTOSet = articleRepository.findArticleByStatus(Status.INACTIVE).stream().map(map -> modelMapper.map(map, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get all processing articles successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOSet);
    }
    @Transactional
    public Response<List<ArticleDTO>> getArticlesOfUser(Integer userId) {
        logger.info("{}{}", GET_ARTICLE_MESSAGE, userId);
        if (userId == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        List<Article> articles = articleRepository.findArticleByMemberIdAndStatus(userId, Status.ACTIVE);
        if (articles.isEmpty()) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        List<ArticleDTO> articleDTOSet = articles.stream().map(article -> modelMapper.map(article, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get articles of user successfully");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOSet);
    }

    @Transactional
    public Response<List<ArticleDTO>> getArticlesByAuthor(Integer userId) {
        logger.info("{}", GET_ARTICLE_MESSAGE);
        logger.info("Author: {}", userId);
        if (userId == null) {
            logger.warn("{}{}", GET_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        List<ArticleDTO> articleDTOS = articleRepository.findArticleByMemberIdAndStatus(userId, Status.ACTIVE).stream().map(article -> modelMapper.map(article, ArticleDTO.class)).collect(Collectors.toList());
        logger.info("Get articles of author {} successfully", userId);
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), articleDTOS);
    }

    public Response<Void> updateArticle(ArticleDTO articleDTO, Integer userId) {
        logger.info("{}{} by user id: {}", UPDATE_ARTICLE_MESSAGE, articleDTO, userId);
        if (articleDTO == null) {
            logger.warn("{}{}", UPDATE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        if (userId == null) {
            logger.warn("{}{}", DELETE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Article article = articleRepository.findArticleByIdAndStatusIsNot(articleDTO.getId(), Status.INACTIVE);
        if (article == null) {
            logger.warn("{}{}", UPDATE_ARTICLE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        if (!userId.equals(article.getMember().getId())) {
            logger.warn("{}User have no permission.", UPDATE_ARTICLE_MESSAGE);
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name());
        }
        if (articleDTO.getAuthor() != null) article.setAuthor(articleDTO.getAuthor());
        if (articleDTO.getDescription() != null) article.setDescription(articleDTO.getDescription());
        if (articleDTO.getContent() != null) article.setContent(articleDTO.getContent());
        if (articleDTO.getGenreId() != null) article.setGenre(genreRepository.findGenreById(articleDTO.getId()));
        if (articleDTO.getLocation() != null) article.setLocation(articleDTO.getLocation());
        if (articleDTO.getImageUrl() != null) article.setImageUrl(articleDTO.getImageUrl());
        if (articleDTO.getTitle() != null) article.setTitle(articleDTO.getTitle());
        if (articleDTO.getMemberId() != null) article.setMember(memberRepository.findMemberById(articleDTO.getId()));
        articleRepository.save(article);
        logger.info("Update Article successfully.");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> deleteArticleById(Integer id, Integer userId) {
        logger.info("{}{}", DELETE_ARTICLE_MESSAGE, id);
        if (userId == null) {
            logger.warn("{}{}", DELETE_ARTICLE_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Article articleEntity = articleRepository.findArticleByIdAndStatusIsNot(id, Status.INACTIVE);
        Member member = memberRepository.findMemberById(userId);
        if (articleEntity == null) {
            logger.warn("{}{}", DELETE_ARTICLE_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(HttpStatus.NOT_FOUND.value(), ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        if (!userId.equals(articleEntity.getMember().getId()) && !member.getRole().equals(Role.MANAGER)) {
            logger.warn("{}User have no permission.", DELETE_ARTICLE_MESSAGE);
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name());
        }
        articleEntity.setStatus(Status.INACTIVE);
        articleRepository.save(articleEntity);
        if (member.getRole().equals(Role.MANAGER))
            logger.info("Delete Article by Manager successfully. Deleter id : {}", userId);
        else logger.info("Delete Article by member successfully. Deleter id : {}", userId);
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }
}

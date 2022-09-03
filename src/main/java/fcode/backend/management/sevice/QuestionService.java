
package fcode.backend.management.sevice;

import fcode.backend.management.model.dto.QuestionDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.repository.QuestionRepository;
import fcode.backend.management.repository.entity.Question;
import fcode.backend.management.sevice.constant.ServiceMessage;
import fcode.backend.management.sevice.constant.ServiceStatusCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(QuestionService.class);
    private static final String CREATE_QUESTION_MESSAGE = "Create question: ";
    private static final String GET_QUESTION_BY_ID_MESSAGE = "Get question by id: ";
    private static final String GET_QUESTION_BY_AUTHOR_MESSAGE = "Get question by author: ";
    private static final String APPROVE_QUESTION = "Approve question: ";
    private static final String UPDATE_QUESTION = "Update question: ";
    private static final String DELETE_QUESTION = "Delete question: ";

    private static final String INACTIVE_STATUS = "Inactive";
    public Response<Void> createQuestion(QuestionDTO questionDTO) {
        logger.info("{}{}", CREATE_QUESTION_MESSAGE, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", CREATE_QUESTION_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question question = modelMapper.map(questionDTO, Question.class);
        question.setId(null);
        question.setStatus("Processing");
        logger.info("{}{}", CREATE_QUESTION_MESSAGE, question);
        questionRepository.save(question);
        logger.info("Create question successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Set<QuestionDTO>> getAllQuestions() {
        logger.info("Get All Questions");
        Set<QuestionDTO> questionDTOSet = questionRepository.findAllQuestion().stream().map(question -> modelMapper.map(question, QuestionDTO.class)).collect(Collectors.toSet());
        logger.info("Get All Questions successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage(), questionDTOSet);
    }
    public Response<QuestionDTO> getQuestionById(Integer id) {
        logger.info("{}{}", GET_QUESTION_BY_ID_MESSAGE, id);
        if (id == null) {
            logger.warn("{}{}", GET_QUESTION_BY_ID_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question question = questionRepository.findQuestionById(id);
        if (question == null) {
            logger.warn("{}{}", GET_QUESTION_BY_ID_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
        logger.info("Get question by Id successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage(), questionDTO);
    }

    public Response<Set<QuestionDTO>> getQuestionByAuthor(String authorEmail) {
        logger.info("{}{}", GET_QUESTION_BY_AUTHOR_MESSAGE, authorEmail);
        if (authorEmail == null) {
            logger.warn("{}{}", GET_QUESTION_BY_AUTHOR_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Set<Question> questions = questionRepository.findQuestionByAuthorEmail(authorEmail);
        if (questions.isEmpty()) {
            logger.warn("{}{}", GET_QUESTION_BY_AUTHOR_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        Set<QuestionDTO> questionDTOSet = questions.stream().map(question -> modelMapper.map(question, QuestionDTO.class)).collect(Collectors.toSet());
        logger.info("Get question by author successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage(), questionDTOSet);
    }


    public Response<Void> approveQuestion(QuestionDTO questionDTO) {
        logger.info("{}{}", APPROVE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", APPROVE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToApproveById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", APPROVE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }

        questionEntity.setStatus("Active");
        questionRepository.save(questionEntity);
        logger.info("Approve question successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> disapproveQuestion(QuestionDTO questionDTO) {
        logger.info("{}{}", APPROVE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", APPROVE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToApproveById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", APPROVE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }

        questionEntity.setStatus(INACTIVE_STATUS);
        questionRepository.save(questionEntity);
        logger.info("Disapprove question successfully");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> updateContent(QuestionDTO questionDTO, String content) {
        logger.info("{}{}", UPDATE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToModifyById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        if (content != null) {
            questionEntity .setContent(content);
            questionEntity.setUpdatedTime(new Date());
        }
        questionRepository.save(questionEntity);
        logger.info("Update content of question successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> updateTitle(QuestionDTO questionDTO, String title) {
        logger.info("{}{}", UPDATE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToModifyById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        if (title != null) {
            questionEntity.setTitle(title);
            questionEntity.setUpdatedTime(new Date());
        }
        questionRepository.save(questionEntity);
        logger.info("Update title of question successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> updateQuestion(QuestionDTO questionDTO, String title, String content) {
        logger.info("{}{}", UPDATE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToModifyById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", UPDATE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        if (title != null)
            questionEntity.setTitle(title);
        if (content != null)
            questionEntity.setContent(content);
        if (title != null || content != null)
            questionEntity.setUpdatedTime(new Date());
        questionRepository.save(questionEntity);
        logger.info("Update question of question successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> deleteQuestion(QuestionDTO questionDTO) {
        logger.info("{}{}", DELETE_QUESTION, questionDTO);
        if (questionDTO == null) {
            logger.warn("{}{}", DELETE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Question questionEntity = questionRepository.findQuestionToModifyById(questionDTO.getId());
        if (questionEntity == null) {
            logger.warn("{}{}", DELETE_QUESTION, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE.getMessage());
        }
        questionEntity.setStatus(INACTIVE_STATUS);
        questionRepository.save(questionEntity);
        logger.info("Delete Question successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }
    public Response<Void> deleteQuestionByAuthorEmail(String authorEmail) {
        logger.info("Delete all question if author is banned.");
        if (authorEmail == null) {
            logger.warn("{}{}", DELETE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        var questions = questionRepository.findQuestionToDeleteByAuthorEmail(authorEmail);
        questions.forEach(question -> {
            question.setStatus(INACTIVE_STATUS);
            questionRepository.save(question);
        });

        logger.info("Delete all question of a author successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }

    public Response<Void> restoreQuestionByAuthorEmail(String authorEmail) {
        logger.info("Restore all question if author is unbanned.");
        if (authorEmail == null) {
            logger.warn("{}{}", DELETE_QUESTION, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        var questions = questionRepository.findQuestionToRestoreByAuthorEmail(authorEmail);
        questions.forEach(question -> {
            question.setStatus("Processing");
            questionRepository.save(question);
        });

        logger.info("Restore all question of a author successfully.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }
}

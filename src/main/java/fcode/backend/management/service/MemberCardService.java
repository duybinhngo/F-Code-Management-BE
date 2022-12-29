package fcode.backend.management.service;

import fcode.backend.management.model.dto.MemberCardDTO;
import fcode.backend.management.model.dto.MemberDTO;
import fcode.backend.management.model.dto.ResourceDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.repository.MemberCardRepository;
import fcode.backend.management.repository.MemberRepository;
import fcode.backend.management.repository.entity.Member;
import fcode.backend.management.repository.entity.MemberCard;
import fcode.backend.management.repository.entity.Resource;
import fcode.backend.management.repository.entity.Subject;
import fcode.backend.management.service.constant.ServiceMessage;
import fcode.backend.management.service.constant.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Calendar;
import java.util.TimeZone;

@Service
public class MemberCardService {
    @Autowired
    MemberCardRepository memberCardRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(MemberCardService.class);
    private static final String CREATE_MEMBER_CARD = "Create member card: ";
    private static final String MEMBER_NOT_EXIST = "Member not exist.";
    private static final String MEMBER_CARD_IS_ALREADY_EXISTED = "Member card is already existed.";

    public Response<MemberDTO> getMemberByCardHashCode(String cardHashCode) {
        logger.info("getMemberByCardHashCode(cardHashCode: {})", cardHashCode);

        MemberCard memberCard = memberCardRepository.getMemberCardByCardHashCode(cardHashCode);
        if(memberCard == null) {
            logger.warn("{}{}", "get Member By CardHashCode: ", ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Member member = memberCard.getMember();
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);
        Date date = member.getClubEntryDate();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        cal.setTime(date);
        int generationYear = cal.get(Calendar.YEAR) - 2004;
        memberDTO.setGenerationYear(generationYear);

        logger.info("{}{}", "get Member By CardHashCode: ", ServiceMessage.SUCCESS_MESSAGE.getMessage());
        return  new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage(), memberDTO);
    }

    @Transactional
    public Response<Void> createMemberCard(MemberCardDTO memberCardDto) {
        logger.info("createMemberCard(memberCardDTO: {})", memberCardDto);

        if(memberCardDto == null) {
            logger.warn("{}{}", CREATE_MEMBER_CARD, ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
            return new Response<>(HttpStatus.BAD_REQUEST.value(), ServiceMessage.INVALID_ARGUMENT_MESSAGE.getMessage());
        }
        Member member = memberRepository.findMemberByIdAndStatus(memberCardDto.getMemberId(), Status.ACTIVE);
        if(member == null) {
            logger.warn("{}{}", CREATE_MEMBER_CARD, MEMBER_NOT_EXIST);
            return new Response<>(HttpStatus.NOT_FOUND.value(), MEMBER_NOT_EXIST);
        }
        if(memberCardRepository.getMemberCardByMemberId(member.getId()) != null) {
            logger.warn("{}{}", CREATE_MEMBER_CARD, MEMBER_CARD_IS_ALREADY_EXISTED);
            return new Response<>(HttpStatus.BAD_REQUEST.value(), MEMBER_CARD_IS_ALREADY_EXISTED);
        }
        MemberCard memberCard = modelMapper.map(memberCardDto, MemberCard.class);
        memberCard.setMember(member);
        memberCardRepository.save(memberCard);
        logger.info("Create member card success");
        return new Response<>(HttpStatus.OK.value(), ServiceMessage.SUCCESS_MESSAGE.getMessage());
    }
}

package fcode.backend.management.controller;

import fcode.backend.management.model.dto.MemberCardDTO;
import fcode.backend.management.model.dto.MemberDTO;
import fcode.backend.management.model.response.Response;
import fcode.backend.management.service.MemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
public class MemberCardController {
    @Autowired
    MemberCardService memberCardService;

    @GetMapping("/{cardHashCode}")
    public Response<MemberDTO> getOneByCardHashCode(@PathVariable String cardHashCode) {
        return memberCardService.getMemberByCardHashCode(cardHashCode);
    }

    @PostMapping
    public Response<Void> createMemberCard(@RequestBody MemberCardDTO memberCardDTO) {
        return  memberCardService.createMemberCard(memberCardDTO);
    }
}

package fcode.backend.management.repository;

import fcode.backend.management.repository.entity.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCardRepository extends JpaRepository<MemberCard, Integer> {
    @Query("select m from MemberCard m where m.cardHashCode = ?1")
    MemberCard getMemberCardByCardHashCode(String cardHashCode);

    @Query(nativeQuery = true, value = "SELECT * FROM member_card WHERE member_id = ?1")
    MemberCard getMemberCardByMemberId(int memberId);
}

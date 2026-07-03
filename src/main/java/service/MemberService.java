package service;

import java.util.List;
import model.Member;

public interface MemberService {
    Member login(String username, String password) throws Exception;
    void register(Member member, String confirmPassword) throws Exception;
    List<Member> getAllMembers();
    List<Member> searchMembers(String keyword);
    void addMember(Member member) throws Exception;
    void updateMember(Member member) throws Exception;
    void deleteMember(int memberId) throws Exception;
    int countMembers();
}

package dao;

import java.util.List;
import java.util.Optional;
import model.Member;

public interface MemberDao {
    List<Member> findAll();
    List<Member> search(String keyword);
    Optional<Member> findById(int memberId);
    Optional<Member> findByUsername(String username);
    Optional<Member> login(String username, String password);
    boolean existsByUsername(String username);
    boolean insert(Member member);
    boolean update(Member member);
    boolean delete(int memberId);
    int countAll();
    int countAdmins();
}

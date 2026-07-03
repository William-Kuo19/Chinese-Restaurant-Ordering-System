package service.impl;

import dao.MemberDao;
import dao.impl.MemberDaoImpl;
import exception.LoginException;
import exception.MemberException;
import model.Member;
import service.MemberService;
import util.Validator;

import java.util.List;

public class MemberServiceImpl implements MemberService {
    private final MemberDao memberDao = new MemberDaoImpl();

    @Override
    public Member login(String username, String password) throws Exception {
        if (Validator.isBlank(username) || Validator.isBlank(password)) throw new LoginException("帳號與密碼不可空白");
        return memberDao.login(username.trim(), password.trim()).orElseThrow(() -> new LoginException("帳號或密碼錯誤"));
    }

    @Override
    public void register(Member member, String confirmPassword) throws Exception {
        if (Validator.isBlank(member.getUsername())) throw new MemberException("帳號不可空白");
        if (Validator.isBlank(member.getPassword())) throw new MemberException("密碼不可空白");
        if (!member.getPassword().equals(confirmPassword)) throw new MemberException("兩次密碼不一致");
        if (!Validator.isEmail(member.getEmail())) throw new MemberException("Email 格式不正確");
        if (memberDao.existsByUsername(member.getUsername().trim())) throw new MemberException("帳號已存在");
        member.setRole("USER");
        if (!memberDao.insert(member)) throw new MemberException("註冊失敗，請確認資料庫連線");
    }

    @Override
    public List<Member> getAllMembers() { return memberDao.findAll(); }

    @Override
    public List<Member> searchMembers(String keyword) { return Validator.isBlank(keyword) ? memberDao.findAll() : memberDao.search(keyword); }

    @Override
    public void addMember(Member member) throws Exception {
        validateMember(member, true);
        if (memberDao.existsByUsername(member.getUsername().trim())) throw new MemberException("帳號已存在");
        if (!memberDao.insert(member)) throw new MemberException("新增會員失敗");
    }

    @Override
    public void updateMember(Member member) throws Exception {
        validateMember(member, false);
        Member existing = memberDao.findByUsername(member.getUsername().trim()).orElse(null);
        if (existing != null && !existing.getMemberId().equals(member.getMemberId())) throw new MemberException("帳號已存在");
        if (!memberDao.update(member)) throw new MemberException("修改會員失敗");
    }

    @Override
    public void deleteMember(int memberId) throws Exception {
        Member member = memberDao.findById(memberId).orElseThrow(() -> new MemberException("找不到會員"));
        if ("admin".equalsIgnoreCase(member.getUsername())) throw new MemberException("預設 admin 帳號不可刪除");
        if ("ADMIN".equalsIgnoreCase(member.getRole()) && memberDao.countAdmins() <= 1) throw new MemberException("不可刪除最後一位管理員");
        if (!memberDao.delete(memberId)) throw new MemberException("刪除會員失敗，可能已有訂單資料關聯");
    }

    @Override
    public int countMembers() { return memberDao.countAll(); }

    private void validateMember(Member member, boolean isNew) throws MemberException {
        if (Validator.isBlank(member.getUsername())) throw new MemberException("帳號不可空白");
        if (Validator.isBlank(member.getPassword())) throw new MemberException("密碼不可空白");
        if (Validator.isBlank(member.getName())) throw new MemberException("姓名不可空白");
        if (!Validator.isEmail(member.getEmail())) throw new MemberException("Email 格式不正確");
        if (!"ADMIN".equals(member.getRole()) && !"USER".equals(member.getRole())) throw new MemberException("權限只能是 ADMIN 或 USER");
    }
}

package com.chen.train.member.domain;

public class Member {
    private Integer memberId;

    private String memberName;

    private Long memberNumber;

    private String memberSex;

    private Integer memberAge;

    private String memberSpecialized;

    private String memberGrade;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Long memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getMemberSex() {
        return memberSex;
    }

    public void setMemberSex(String memberSex) {
        this.memberSex = memberSex;
    }

    public Integer getMemberAge() {
        return memberAge;
    }

    public void setMemberAge(Integer memberAge) {
        this.memberAge = memberAge;
    }

    public String getMemberSpecialized() {
        return memberSpecialized;
    }

    public void setMemberSpecialized(String memberSpecialized) {
        this.memberSpecialized = memberSpecialized;
    }

    public String getMemberGrade() {
        return memberGrade;
    }

    public void setMemberGrade(String memberGrade) {
        this.memberGrade = memberGrade;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", memberId=").append(memberId);
        sb.append(", memberName=").append(memberName);
        sb.append(", memberNumber=").append(memberNumber);
        sb.append(", memberSex=").append(memberSex);
        sb.append(", memberAge=").append(memberAge);
        sb.append(", memberSpecialized=").append(memberSpecialized);
        sb.append(", memberGrade=").append(memberGrade);
        sb.append("]");
        return sb.toString();
    }
}
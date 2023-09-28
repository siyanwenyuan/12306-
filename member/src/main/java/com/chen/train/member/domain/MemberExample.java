package com.chen.train.member.domain;

import java.util.ArrayList;
import java.util.List;

public class MemberExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MemberExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andMemberIdIsNull() {
            addCriterion("member_id is null");
            return (Criteria) this;
        }

        public Criteria andMemberIdIsNotNull() {
            addCriterion("member_id is not null");
            return (Criteria) this;
        }

        public Criteria andMemberIdEqualTo(Integer value) {
            addCriterion("member_id =", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotEqualTo(Integer value) {
            addCriterion("member_id <>", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThan(Integer value) {
            addCriterion("member_id >", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("member_id >=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThan(Integer value) {
            addCriterion("member_id <", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThanOrEqualTo(Integer value) {
            addCriterion("member_id <=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdIn(List<Integer> values) {
            addCriterion("member_id in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotIn(List<Integer> values) {
            addCriterion("member_id not in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdBetween(Integer value1, Integer value2) {
            addCriterion("member_id between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotBetween(Integer value1, Integer value2) {
            addCriterion("member_id not between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberNameIsNull() {
            addCriterion("member_name is null");
            return (Criteria) this;
        }

        public Criteria andMemberNameIsNotNull() {
            addCriterion("member_name is not null");
            return (Criteria) this;
        }

        public Criteria andMemberNameEqualTo(String value) {
            addCriterion("member_name =", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameNotEqualTo(String value) {
            addCriterion("member_name <>", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameGreaterThan(String value) {
            addCriterion("member_name >", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameGreaterThanOrEqualTo(String value) {
            addCriterion("member_name >=", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameLessThan(String value) {
            addCriterion("member_name <", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameLessThanOrEqualTo(String value) {
            addCriterion("member_name <=", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameLike(String value) {
            addCriterion("member_name like", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameNotLike(String value) {
            addCriterion("member_name not like", value, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameIn(List<String> values) {
            addCriterion("member_name in", values, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameNotIn(List<String> values) {
            addCriterion("member_name not in", values, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameBetween(String value1, String value2) {
            addCriterion("member_name between", value1, value2, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNameNotBetween(String value1, String value2) {
            addCriterion("member_name not between", value1, value2, "memberName");
            return (Criteria) this;
        }

        public Criteria andMemberNumberIsNull() {
            addCriterion("member_number is null");
            return (Criteria) this;
        }

        public Criteria andMemberNumberIsNotNull() {
            addCriterion("member_number is not null");
            return (Criteria) this;
        }

        public Criteria andMemberNumberEqualTo(Long value) {
            addCriterion("member_number =", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberNotEqualTo(Long value) {
            addCriterion("member_number <>", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberGreaterThan(Long value) {
            addCriterion("member_number >", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberGreaterThanOrEqualTo(Long value) {
            addCriterion("member_number >=", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberLessThan(Long value) {
            addCriterion("member_number <", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberLessThanOrEqualTo(Long value) {
            addCriterion("member_number <=", value, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberIn(List<Long> values) {
            addCriterion("member_number in", values, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberNotIn(List<Long> values) {
            addCriterion("member_number not in", values, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberBetween(Long value1, Long value2) {
            addCriterion("member_number between", value1, value2, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberNumberNotBetween(Long value1, Long value2) {
            addCriterion("member_number not between", value1, value2, "memberNumber");
            return (Criteria) this;
        }

        public Criteria andMemberSexIsNull() {
            addCriterion("member_sex is null");
            return (Criteria) this;
        }

        public Criteria andMemberSexIsNotNull() {
            addCriterion("member_sex is not null");
            return (Criteria) this;
        }

        public Criteria andMemberSexEqualTo(String value) {
            addCriterion("member_sex =", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexNotEqualTo(String value) {
            addCriterion("member_sex <>", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexGreaterThan(String value) {
            addCriterion("member_sex >", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexGreaterThanOrEqualTo(String value) {
            addCriterion("member_sex >=", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexLessThan(String value) {
            addCriterion("member_sex <", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexLessThanOrEqualTo(String value) {
            addCriterion("member_sex <=", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexLike(String value) {
            addCriterion("member_sex like", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexNotLike(String value) {
            addCriterion("member_sex not like", value, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexIn(List<String> values) {
            addCriterion("member_sex in", values, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexNotIn(List<String> values) {
            addCriterion("member_sex not in", values, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexBetween(String value1, String value2) {
            addCriterion("member_sex between", value1, value2, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberSexNotBetween(String value1, String value2) {
            addCriterion("member_sex not between", value1, value2, "memberSex");
            return (Criteria) this;
        }

        public Criteria andMemberAgeIsNull() {
            addCriterion("member_age is null");
            return (Criteria) this;
        }

        public Criteria andMemberAgeIsNotNull() {
            addCriterion("member_age is not null");
            return (Criteria) this;
        }

        public Criteria andMemberAgeEqualTo(Integer value) {
            addCriterion("member_age =", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeNotEqualTo(Integer value) {
            addCriterion("member_age <>", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeGreaterThan(Integer value) {
            addCriterion("member_age >", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeGreaterThanOrEqualTo(Integer value) {
            addCriterion("member_age >=", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeLessThan(Integer value) {
            addCriterion("member_age <", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeLessThanOrEqualTo(Integer value) {
            addCriterion("member_age <=", value, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeIn(List<Integer> values) {
            addCriterion("member_age in", values, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeNotIn(List<Integer> values) {
            addCriterion("member_age not in", values, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeBetween(Integer value1, Integer value2) {
            addCriterion("member_age between", value1, value2, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberAgeNotBetween(Integer value1, Integer value2) {
            addCriterion("member_age not between", value1, value2, "memberAge");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedIsNull() {
            addCriterion("member_specialized is null");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedIsNotNull() {
            addCriterion("member_specialized is not null");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedEqualTo(String value) {
            addCriterion("member_specialized =", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedNotEqualTo(String value) {
            addCriterion("member_specialized <>", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedGreaterThan(String value) {
            addCriterion("member_specialized >", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedGreaterThanOrEqualTo(String value) {
            addCriterion("member_specialized >=", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedLessThan(String value) {
            addCriterion("member_specialized <", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedLessThanOrEqualTo(String value) {
            addCriterion("member_specialized <=", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedLike(String value) {
            addCriterion("member_specialized like", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedNotLike(String value) {
            addCriterion("member_specialized not like", value, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedIn(List<String> values) {
            addCriterion("member_specialized in", values, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedNotIn(List<String> values) {
            addCriterion("member_specialized not in", values, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedBetween(String value1, String value2) {
            addCriterion("member_specialized between", value1, value2, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberSpecializedNotBetween(String value1, String value2) {
            addCriterion("member_specialized not between", value1, value2, "memberSpecialized");
            return (Criteria) this;
        }

        public Criteria andMemberGradeIsNull() {
            addCriterion("member_grade is null");
            return (Criteria) this;
        }

        public Criteria andMemberGradeIsNotNull() {
            addCriterion("member_grade is not null");
            return (Criteria) this;
        }

        public Criteria andMemberGradeEqualTo(String value) {
            addCriterion("member_grade =", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeNotEqualTo(String value) {
            addCriterion("member_grade <>", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeGreaterThan(String value) {
            addCriterion("member_grade >", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeGreaterThanOrEqualTo(String value) {
            addCriterion("member_grade >=", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeLessThan(String value) {
            addCriterion("member_grade <", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeLessThanOrEqualTo(String value) {
            addCriterion("member_grade <=", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeLike(String value) {
            addCriterion("member_grade like", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeNotLike(String value) {
            addCriterion("member_grade not like", value, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeIn(List<String> values) {
            addCriterion("member_grade in", values, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeNotIn(List<String> values) {
            addCriterion("member_grade not in", values, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeBetween(String value1, String value2) {
            addCriterion("member_grade between", value1, value2, "memberGrade");
            return (Criteria) this;
        }

        public Criteria andMemberGradeNotBetween(String value1, String value2) {
            addCriterion("member_grade not between", value1, value2, "memberGrade");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
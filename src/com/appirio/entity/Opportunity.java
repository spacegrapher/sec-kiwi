package com.appirio.entity;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Opportunity {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) Long id;
    @Persistent private String name;
    @Persistent private double amount;
    @Persistent private String stageName;
    @Persistent private int probability;
    @Persistent private Date closeDate;
    @Persistent private int orderNumber;
    @Persistent private Long accountId;

    public Opportunity(String name, double amount, String stageName,
            int probability, Date closeDate, int orderNumber, Long accountId) {
        this.name = name;
        this.amount = amount;
        this.stageName = stageName;
        this.probability = probability;
        this.closeDate = closeDate;
        this.orderNumber = orderNumber;
        this.accountId = accountId;
    }

    /** 
     * @return 식별자
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id 지정할 식별자
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 이름
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 지정할 이름
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return 총액
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount 지정할 총액
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return 단계
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * @param stageName 지정할 단계
     */
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    /**
     * @return 가능성
     */
    public int getProbability() {
        return probability;
    }

    /**
     * @param probability 지정할 가능성
     */
    public void setProbability(int probability) {
        this.probability = probability;
    }

    /**
     * @return 마감일
     */
    public Date getCloseDate() {
        return closeDate;
    }

    /**
     * @param closeDate 지정할 마감일
     */
    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    /**
     * @return 주문량
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * @param orderNumber 지정할 주문량
     */
    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * @return 계정 고유번호
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId 지정할 계정 고유번호
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
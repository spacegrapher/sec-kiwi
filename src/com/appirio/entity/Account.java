package com.appirio.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Account {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) private Long id;
    @Persistent private String name;
    @Persistent private String city;
    @Persistent private String state;
    @Persistent private String phone;
    @Persistent String website;

    public Account(String name, String city, String state, String phone,
            String website) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.website = website;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
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
     * @return 도시
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city 지정할 도시
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return 주(州)
     */
    public String getState() {
        return state;
    }

    /**
     * @param state 지정할 주(州)
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return 전화번호
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone 지정할 전화번호
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return 웹사이트
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website 지정할 웹 사이트
     */
    public void setWebsite(String website) {
        this.website = website;
    }
}
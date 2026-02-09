package com.blaybus.blaybusbe.domain.user.entity;

import com.blaybus.blaybusbe.domain.user.dto.request.RequestUpdateUserDto;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username; // 로그인 ID

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // MENTOR, MENTEE

    @Column(columnDefinition = "TEXT")
    private String profileImgUrl;

    private String fcmToken;

    @Column(nullable = false)
    private Boolean isAlarmEnabled = true;

    @Builder
    public User(String username, String password, String name, String nickname, Role role, String profileImgUrl) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
        this.profileImgUrl = profileImgUrl;
    }

    public void updateUser(RequestUpdateUserDto dto){
        if(dto.name() != null) this.name = dto.name();
        if(dto.nickName() != null) this.nickname = dto.nickName();
    }
}

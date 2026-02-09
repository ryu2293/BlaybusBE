SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE mentee_info;
TRUNCATE TABLE users;

-- 1. 사용자 데이터
INSERT INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
VALUES (1, 'mentor1', '1234', '멘토1', '멘토1', 'MENTOR', true);

INSERT INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
VALUES (2, 'mentee1', '1234', '멘티1', '멘티1', 'MENTEE', true);

INSERT INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
VALUES (3, 'mentee2', '123', '멘티2', '이건 두번째 멘티', 'MENTEE', true);

-- 2. 멘티 정보
INSERT INTO mentee_info (id, school_name, mentor_id, mentee_id)
VALUES (1, '테스트고등학교', 1, 2);

INSERT INTO mentee_info (id, school_name, mentor_id, mentee_id)
VALUES (2, '강대근고등학교', 1, 3);

SET FOREIGN_KEY_CHECKS = 1;
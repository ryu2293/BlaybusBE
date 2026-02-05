SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE mentee_info;
TRUNCATE TABLE users;

INSERT INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
VALUES (1, 'mentor1', '1234', '멘토1', '멘토닉네임', 'MENTOR', true);

INSERT INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
VALUES (2, 'mentee1', '1234', '멘토1', '멘토닉네임', 'MENTEE', true);

INSERT INTO mentee_info (id, school_name, mentor_id, mentee_id)
VALUES (1, '테스트고등학교', 1, 2);

SET REFERENTIAL_INTEGRITY TRUE;
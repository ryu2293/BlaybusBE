-- 초기 테스트 데이터 (H2 재시작 시 자동 삽입)
-- MERGE INTO: 이미 존재하면 무시, 없으면 삽입

MERGE INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
KEY (username)
VALUES (1, 'mentor1', '1234', '멘토1', '멘토닉네임', 'MENTOR', true);

MERGE INTO users (user_id, username, password, name, nickname, role, is_alarm_enabled)
KEY (username)
VALUES (2, 'mentee1', '1234', '멘티1', '멘티닉네임', 'MENTEE', true);

-- 멘토-멘티 매핑
MERGE INTO mentee_info (id, school_name, mentor_id, mentee_id)
KEY (mentee_id)
VALUES (1, '테스트고등학교', 1, 2);

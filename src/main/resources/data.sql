-- 유저 테이블에 데이터 삽입
INSERT INTO users (username, password, name, email)
VALUES ('user1', 'password1', '김철수', 'john@example.com'),
       ('user2', 'password2', '이영희', 'jane@example.com'),
       ('user3', 'password3', '박민수', 'alice@example.com'),
       ('user4', 'password4', '최지우', 'bob@example.com'),
       ('user5', 'password5', '한예슬', 'charlie@example.com'),
       ('test', 'test', '테스트', 'test@email.com');

-- 포스트 테이블에 데이터 삽입
INSERT INTO posts (username, title, content)
VALUES ('user1', '첫 번째 포스트', '이것은 첫 번째 포스트의 내용입니다.'),
       ('user2', '두 번째 포스트', '이것은 두 번째 포스트의 내용입니다.'),
       ('user3', '세 번째 포스트', '이것은 세 번째 포스트의 내용입니다.'),
       ('user4', '네 번째 포스트', '이것은 네 번째 포스트의 내용입니다.'),
       ('user5', '다섯 번째 포스트', '이것은 다섯 번째 포스트의 내용입니다.');

-- 댓글 테이블에 데이터 삽입
INSERT INTO comments (post_id, username, content)
VALUES
    (1, 'user2', '멋진 포스트입니다!'),
    (1, 'user3', '공유해 주셔서 감사합니다.'),
    (1, 'user4', '매우 유익합니다.'),
    (1, 'user5', '이 포스트에서 많이 배웠습니다.'),

    (2, 'user1', '좋은 작업입니다!'),
    (2, 'user3', '당신의 의견에 동의합니다.'),
    (2, 'user4', '흥미로운 관점입니다.'),
    (2, 'user5', '잘 썼습니다.'),

    (3, 'user1', '좋은 작업입니다!'),
    (3, 'user2', '이것은 도움이 됩니다.'),
    (3, 'user4', '읽는 재미가 있었습니다.'),
    (3, 'user5', '계속 그렇게 하세요!'),

    (4, 'user1', '훌륭한 포스트입니다.'),
    (4, 'user2', '통찰력 감사합니다.'),
    (4, 'user3', '매우 상세합니다.'),
    (4, 'user5', '이것을 고맙게 생각합니다.'),

    (5, 'user1', '멋진 포스트입니다!'),
    (5, 'user2', '이것은 매우 유용했습니다.'),
    (5, 'user3', '잘 설명되었습니다.'),
    (5, 'user4', '이것이 매우 도움이 되었습니다.');

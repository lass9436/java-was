INSERT INTO users (username, password, name, email)
VALUES ('kimc', 'password1', '김철수', 'john@example.com');
INSERT INTO users (username, password, name, email)
VALUES ('leeyh', 'password2', '이영희', 'jane@example.com');
INSERT INTO users (username, password, name, email)
VALUES ('parkms', 'password3', '박민수', 'alice@example.com');
INSERT INTO users (username, password, name, email)
VALUES ('choijw', 'password4', '최지우', 'bob@example.com');
INSERT INTO users (username, password, name, email)
VALUES ('hanys', 'password5', '한예슬', 'charlie@example.com');
INSERT INTO users (username, password, name, email)
VALUES ('test', 'test', '테스트', 'test@email.com');

INSERT INTO posts (username, title, content, image_url)
VALUES ('kimc', '첫 번째 포스트', '이것은 첫 번째 포스트의 내용입니다.', 'image1.jpg');
INSERT INTO posts (username, title, content, image_url)
VALUES ('leeyh', '두 번째 포스트', '이것은 두 번째 포스트의 내용입니다.', 'image2.jpg');
INSERT INTO posts (username, title, content, image_url)
VALUES ('parkms', '세 번째 포스트', '이것은 세 번째 포스트의 내용입니다.', 'image3.jpg');
INSERT INTO posts (username, title, content, image_url)
VALUES ('choijw', '네 번째 포스트', '이것은 네 번째 포스트의 내용입니다.', 'image4.jpg');
INSERT INTO posts (username, title, content, image_url)
VALUES ('hanys', '다섯 번째 포스트', '이것은 다섯 번째 포스트의 내용입니다.', 'image5.jpg');

INSERT INTO comments (post_id, username, content)
VALUES (1, 'leeyh', '멋진 포스트입니다!');
INSERT INTO comments (post_id, username, content)
VALUES (1, 'parkms', '공유해 주셔서 감사합니다.');
INSERT INTO comments (post_id, username, content)
VALUES (1, 'choijw', '매우 유익합니다.');
INSERT INTO comments (post_id, username, content)
VALUES (1, 'hanys', '이 포스트에서 많이 배웠습니다.');

INSERT INTO comments (post_id, username, content)
VALUES (2, 'kimc', '좋은 작업입니다!');
INSERT INTO comments (post_id, username, content)
VALUES (2, 'parkms', '당신의 의견에 동의합니다.');
INSERT INTO comments (post_id, username, content)
VALUES (2, 'choijw', '흥미로운 관점입니다.');
INSERT INTO comments (post_id, username, content)
VALUES (2, 'hanys', '잘 썼습니다.');

INSERT INTO comments (post_id, username, content)
VALUES (3, 'kimc', '좋은 작업입니다!');
INSERT INTO comments (post_id, username, content)
VALUES (3, 'leeyh', '이것은 도움이 됩니다.');
INSERT INTO comments (post_id, username, content)
VALUES (3, 'choijw', '읽는 재미가 있었습니다.');
INSERT INTO comments (post_id, username, content)
VALUES (3, 'hanys', '계속 그렇게 하세요!');

INSERT INTO comments (post_id, username, content)
VALUES (4, 'kimc', '훌륭한 포스트입니다.');
INSERT INTO comments (post_id, username, content)
VALUES (4, 'leeyh', '통찰력 감사합니다.');
INSERT INTO comments (post_id, username, content)
VALUES (4, 'parkms', '매우 상세합니다.');
INSERT INTO comments (post_id, username, content)
VALUES (4, 'hanys', '이것을 고맙게 생각합니다.');

INSERT INTO comments (post_id, username, content)
VALUES (5, 'kimc', '멋진 포스트입니다!');
INSERT INTO comments (post_id, username, content)
VALUES (5, 'leeyh', '이것은 매우 유용했습니다.');
INSERT INTO comments (post_id, username, content)
VALUES (5, 'parkms', '잘 설명되었습니다.');
INSERT INTO comments (post_id, username, content)
VALUES (5, 'choijw', '이것이 매우 도움이 되었습니다.');

-- users 테이블에 데이터 삽입
INSERT INTO users (username, password, name, email)
VALUES ('user1', 'password1', 'John Doe', 'john@example.com'),
       ('user2', 'password2', 'Jane Smith', 'jane@example.com'),
       ('user3', 'password3', 'Alice Johnson', 'alice@example.com'),
       ('user4', 'password4', 'Bob Brown', 'bob@example.com'),
       ('user5', 'password5', 'Charlie Davis', 'charlie@example.com');

-- posts 테이블에 데이터 삽입
INSERT INTO posts (username, title, content)
VALUES ('user1', 'First Post', 'This is the content of the first post.'),
       ('user2', 'Second Post', 'This is the content of the second post.'),
       ('user3', 'Third Post', 'This is the content of the third post.'),
       ('user4', 'Fourth Post', 'This is the content of the fourth post.'),
       ('user5', 'Fifth Post', 'This is the content of the fifth post.');

-- Message1 (Member2 -> Member1)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url, created_at) VALUES
(1, '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지1', false, 'img_url1', '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url, created_at) VALUES
(2, '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지2', false, 'img_url2', '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url, created_at) VALUES
(3, '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지3', false, 'img_url3', '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url, created_at) VALUES
(4, '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지4', false, 'img_url4', '2022-08-20 09:35:25.560');

-- Message1 (Member1 -> Member2)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, created_at) VALUES
(5, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 2, '메시지1', false, '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, created_at) VALUES
(6, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 2, '메시지2', false, '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, created_at) VALUES
(7, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 2, '메시지3', false, '2022-08-20 09:35:25.560');

INSERT INTO message(message_id, sender_id, receiver_id, room_id, read, img_url, created_at) VALUES
(8, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 2, false, 'img_url4', '2022-08-20 09:35:25.560');

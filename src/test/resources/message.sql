-- Message1 (Member2 -> Member1)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url) VALUES
(1, '4ba90eab-c62b-4d44-aadb-b7f3183ea83e', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지1', false, 'img_url1');

-- Message2 (Member3 -> Member1)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url) VALUES
(2, '19452518-63ed-46fb-b0f0-19c0bdf97d7b', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지2', false, 'img_url2');

-- Message3 (Member4 -> Member1)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url) VALUES
(3, '34c45053-41df-44ef-8f00-e8f3f10b89a9', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지2', false, 'img_url3');

-- Message4 (Member5 -> Member1)
INSERT INTO message(message_id, sender_id, receiver_id, room_id, content, read, img_url) VALUES
(4, 'e42085a8-08c5-45d5-8d58-ec39da152dbe', 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2, '메시지3', true, 'img_url4');


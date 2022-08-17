-- getParticipatingRooms API 용

-- Room
INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(2,'제목2',13, '2022-10-09','2022-10-20', 'XYQOTE', 'POST');

INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(3,'제목3',13, '2022-10-09','2022-10-20', 'XYQOTE', 'PROCESSING');

INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(4,'제목4',13, '2022-10-09','2022-10-20', 'XYQOTE', 'PRE');

INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(5,'제목5',13, '2022-10-09','2022-10-20', 'XYQOTE', 'POST');


-- MemberRoom
INSERT INTO member_room (member_room_id, member_id, room_id, admin, color_idx) VALUES
(1, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 1 , false, 1);

INSERT INTO member_room (member_room_id, member_id, room_id, admin, color_idx) VALUES
(2, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 2 , true, 2);

INSERT INTO member_room (member_room_id, member_id, room_id, admin, color_idx) VALUES
(3, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 3 , true, 3);

INSERT INTO member_room (member_room_id, member_id, room_id, admin, color_idx) VALUES
(4, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 4 , false, 4);

INSERT INTO member_room (member_room_id, member_id, room_id, admin, color_idx) VALUES
(5, 'f383cdb3-a871-4410-b146-fb1f7b447b9e', 5 , false, 5);


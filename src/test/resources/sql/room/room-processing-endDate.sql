--Room1 (PROCESSING - enddate currentdate)
INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(1,'제목1',13, '2022-10-09',CURRENT_DATE, 'XYQOTE', 'PROCESSING');

--Room2 (PROCESSING - enddate farfuture)
INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(2,'제목2',13, '2022-10-09','2027-10-11', 'XYQOTE', 'PROCESSING');

--Room3 (POST)
INSERT INTO room (room_id, title, capacity, start_date, end_date, invitation, state) VALUES
(3,'제목3',13, '2022-10-09','2022-11-11', 'XYQOTE', 'POST');
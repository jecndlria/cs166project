-- Primary keys are already indexed implicitly!

-- We do a range check on room booking dates, so we use a btree index here.
CREATE INDEX room_booking_date_index
ON RoomBookings
USING BTREE (bookingDate);

-- We do equality checks on these fields, so we hash index them here.
CREATE INDEX hotel_lat_index
ON Hotel
USING HASH (latitude);

CREATE INDEX hotel_long_index
ON Hotel
USING HASH (longitude);
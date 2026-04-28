CREATE TEMPORARY TABLE seeded_specialist_ids
SELECT id
FROM specialist
WHERE LOWER(name) IN (
    'dr. ethan zhao',
    'prof. lucy chen',
    'dr. marcus liu',
    'ms. hannah sun',
    'ms. ivy wang',
    'ms. nora xu',
    'mr. daniel gu',
    'prof. helen qiao',
    'dr. adrian yao',
    'ms. sophie lin',
    'dr. rachel zhou'
);

CREATE TEMPORARY TABLE seeded_specialists_with_history
SELECT DISTINCT specialist_id
FROM (
    SELECT booking.specialist_id AS specialist_id
    FROM booking
    WHERE booking.specialist_id IN (SELECT id FROM seeded_specialist_ids)

    UNION

    SELECT feedback.specialist_id AS specialist_id
    FROM feedback
    WHERE feedback.specialist_id IN (SELECT id FROM seeded_specialist_ids)

    UNION

    SELECT app_user.specialist_id AS specialist_id
    FROM app_user
    WHERE app_user.specialist_id IN (SELECT id FROM seeded_specialist_ids)
) history_links;

DELETE FROM availability_slot
WHERE specialist_id IN (
    SELECT id
    FROM seeded_specialist_ids
    WHERE id NOT IN (SELECT specialist_id FROM seeded_specialists_with_history)
);

DELETE FROM app_user
WHERE specialist_id IN (
    SELECT id
    FROM seeded_specialist_ids
    WHERE id NOT IN (SELECT specialist_id FROM seeded_specialists_with_history)
);

DELETE FROM specialist
WHERE id IN (
    SELECT id
    FROM seeded_specialist_ids
    WHERE id NOT IN (SELECT specialist_id FROM seeded_specialists_with_history)
);

DELETE FROM availability_slot
WHERE specialist_id IN (SELECT specialist_id FROM seeded_specialists_with_history)
  AND booked = b'0'
  AND slot_date >= CURRENT_DATE;

UPDATE specialist
SET status = 'INACTIVE'
WHERE id IN (SELECT specialist_id FROM seeded_specialists_with_history);

DROP TEMPORARY TABLE seeded_specialists_with_history;
DROP TEMPORARY TABLE seeded_specialist_ids;

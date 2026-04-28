INSERT INTO expertise_category (name, status)
SELECT 'Computer Science', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Computer Science')
);

INSERT INTO expertise_category (name, status)
SELECT 'Data Science', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Data Science')
);

INSERT INTO expertise_category (name, status)
SELECT 'Artificial Intelligence', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Artificial Intelligence')
);

INSERT INTO expertise_category (name, status)
SELECT 'Software Engineering', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Software Engineering')
);

INSERT INTO expertise_category (name, status)
SELECT 'Business Analytics', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Business Analytics')
);

INSERT INTO expertise_category (name, status)
SELECT 'Accounting', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Accounting')
);

INSERT INTO expertise_category (name, status)
SELECT 'Marketing', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Marketing')
);

INSERT INTO expertise_category (name, status)
SELECT 'Architecture', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Architecture')
);

INSERT INTO expertise_category (name, status)
SELECT 'Urban Planning', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('Urban Planning')
);

INSERT INTO expertise_category (name, status)
SELECT 'International Business', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM expertise_category WHERE LOWER(name) = LOWER('International Business')
);

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Dr. Ethan Zhao',
       'Senior',
       280.00,
       'Consultation on algorithms, programming foundations, and final-year computing project planning.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Computer Science')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Dr. Ethan Zhao')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Prof. Lucy Chen',
       'Consultant',
       330.00,
       'Guidance for statistical modelling, machine learning workflows, and data-driven research design.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Data Science')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Prof. Lucy Chen')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Dr. Marcus Liu',
       'Consultant',
       360.00,
       'Support for AI project scoping, model evaluation, and applied intelligent systems consultation.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Artificial Intelligence')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Dr. Marcus Liu')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Ms. Hannah Sun',
       'Senior',
       290.00,
       'Advice on software lifecycle planning, teamwork delivery, and requirements-to-implementation workflow.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Software Engineering')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Ms. Hannah Sun')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Ms. Ivy Wang',
       'Senior',
       300.00,
       'Consultation on analytics dashboards, business data interpretation, and decision-support use cases.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Business Analytics')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Ms. Ivy Wang')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Ms. Nora Xu',
       'Associate',
       240.00,
       'Support for financial statements, management accounting topics, and academic-to-practice cases.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Accounting')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Ms. Nora Xu')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Mr. Daniel Gu',
       'Senior',
       260.00,
       'Consultation on branding strategy, campaign planning, and consumer insight analysis.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Marketing')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Mr. Daniel Gu')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Prof. Helen Qiao',
       'Consultant',
       340.00,
       'Guidance for architectural studio planning, portfolio refinement, and spatial design review.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Architecture')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Prof. Helen Qiao')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Dr. Adrian Yao',
       'Senior',
       310.00,
       'Consultation on urban systems, planning proposals, and sustainable development case analysis.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Urban Planning')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Dr. Adrian Yao')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Ms. Sophie Lin',
       'Associate',
       250.00,
       'Support for international business case framing, market entry thinking, and cross-border communication.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('International Business')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Ms. Sophie Lin')
  );

INSERT INTO specialist (name, level, fee_rate, profile_description, status, category_id)
SELECT 'Dr. Rachel Zhou',
       'Senior',
       305.00,
       'Consultation on corporate finance, investment analysis, and finance-related capstone topics.',
       'ACTIVE',
       category.id
FROM expertise_category category
WHERE LOWER(category.name) = LOWER('Finance')
  AND NOT EXISTS (
      SELECT 1 FROM specialist WHERE LOWER(name) = LOWER('Dr. Rachel Zhou')
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), '09:00:00', '10:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Dr. Ethan Zhao')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY)
        AND slot.start_time = '09:00:00'
        AND slot.end_time = '10:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), '14:00:00', '15:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Prof. Lucy Chen')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY)
        AND slot.start_time = '14:00:00'
        AND slot.end_time = '15:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), '10:00:00', '11:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Dr. Marcus Liu')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY)
        AND slot.start_time = '10:00:00'
        AND slot.end_time = '11:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY), '13:00:00', '14:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Ms. Hannah Sun')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY)
        AND slot.start_time = '13:00:00'
        AND slot.end_time = '14:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), '11:00:00', '12:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Ms. Ivy Wang')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY)
        AND slot.start_time = '11:00:00'
        AND slot.end_time = '12:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY), '09:30:00', '10:30:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Ms. Nora Xu')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY)
        AND slot.start_time = '09:30:00'
        AND slot.end_time = '10:30:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 9 DAY), '15:00:00', '16:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Mr. Daniel Gu')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 9 DAY)
        AND slot.start_time = '15:00:00'
        AND slot.end_time = '16:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), '10:00:00', '11:30:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Prof. Helen Qiao')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY)
        AND slot.start_time = '10:00:00'
        AND slot.end_time = '11:30:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 11 DAY), '14:30:00', '15:30:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Dr. Adrian Yao')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 11 DAY)
        AND slot.start_time = '14:30:00'
        AND slot.end_time = '15:30:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY), '16:00:00', '17:00:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Ms. Sophie Lin')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY)
        AND slot.start_time = '16:00:00'
        AND slot.end_time = '17:00:00'
  );

INSERT INTO availability_slot (specialist_id, slot_date, start_time, end_time, booked)
SELECT specialist.id, DATE_ADD(CURRENT_DATE, INTERVAL 13 DAY), '11:30:00', '12:30:00', b'0'
FROM specialist
WHERE LOWER(specialist.name) = LOWER('Dr. Rachel Zhou')
  AND NOT EXISTS (
      SELECT 1
      FROM availability_slot slot
      WHERE slot.specialist_id = specialist.id
        AND slot.slot_date = DATE_ADD(CURRENT_DATE, INTERVAL 13 DAY)
        AND slot.start_time = '11:30:00'
        AND slot.end_time = '12:30:00'
  );

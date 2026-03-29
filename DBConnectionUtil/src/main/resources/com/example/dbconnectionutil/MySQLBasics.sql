-- Access PhpMyAdmin for MySQL. Make sure to start Apache since MySQL requires it.
-- Link: http://localhost:8080/phpmyadmin/

CREATE DATABASE IF NOT EXISTS learnjavafx;
USE learnjavafx;

CREATE TABLE trainees (
    rollno INT PRIMARY KEY,
    sname VARCHAR(40),
    per FLOAT,
    branch VARCHAR(10),
    mobile VARCHAR(12)
);

-- Q- Can we use rollNo as rollNo. i.e. use special characters?
-- A- Only letters, numbers, and underscores (_) are allowed along with other specific symbols depending on the database system

-- Each SQL command must end with a semicolon (;)

INSERT INTO trainees (rollno, sname, per, branch, mobile)
VALUES (101, 'Aman', 65, 'CSE', '9876543210');

INSERT INTO trainees (rollno, sname, per, branch, mobile)
VALUES (102, 'Raman', 80, 'ECE', '9876543111');

-- Partial insertion (i.e. insertion in specific columns)
INSERT INTO trainees (rollno, sname, mobile)
VALUES (103, 'Harry', '1111122222');

UPDATE trainees
SET branch = 'ECE', per = 90
WHERE rollno = 103;

SELECT * FROM trainees;

-- Giving grace percentage to all students having less than or equal to 70%
UPDATE trainees
SET per = per + 5
WHERE per <= 70;

SELECT * FROM trainees
WHERE sname = 'Aman';

INSERT INTO trainees (rollno, sname, per, branch, mobile)
VALUES (105, 'Chaman', 91, 'MECH', '9090901111');

SELECT * FROM trainees
WHERE sname = 'Aman' AND branch = 'ECE';

-- Using OR operator
SELECT * FROM trainees
WHERE sname = 'Aman' OR branch = 'ECE';

-- Using IN operator
SELECT * FROM trainees
WHERE branch IN ('MECH', 'CSE');

-- Using NOT EQUAL TO operator (<>, !=)
SELECT * FROM trainees
WHERE branch <> 'CSE';

-- Displaying only these columns
SELECT rollno, sname, per FROM trainees;

-- Sorting on the basis of sname and retrieving specific columns for all records
SELECT rollno, sname, per FROM trainees
ORDER BY sname;

-- Sorting on the basis of per and retrieving specific columns for all records
SELECT rollno, sname, per FROM trainees
ORDER BY per;

-- Sorting in descending order of percentage
SELECT rollno, sname, per FROM trainees
ORDER BY per DESC;

-- Displaying top 2 students
SELECT * FROM trainees
LIMIT 2;

-- Displaying top 2 highest percentage students
SELECT rollno, sname, per FROM trainees
ORDER BY per DESC
LIMIT 2;

-- Selecting all names starting with 'A'
SELECT * FROM trainees
WHERE sname LIKE 'A%';

-- Selecting all names ending with 'an'
SELECT * FROM trainees
WHERE sname LIKE '%an';

-- Selecting all names having 'ma' in between
SELECT * FROM trainees
WHERE sname LIKE '%ma%';

-- Find the maximum, total, average, and minimum percentage
SELECT
    MAX(per) AS 'Maximum Percentage',
    SUM(per) AS 'Total Percentage',
    AVG(per) AS 'Average Percentage',
    MIN(per) AS 'Minimum Percentage'
FROM trainees;

-- Subquery (nested query)
-- Find the details of the student having the highest percentage
SELECT *
FROM trainees
WHERE per = (SELECT MAX(per) FROM trainees);

SELECT MAX(PER)
FROM TRAINEES
WHERE BRANCH = 'ECE';

-- [INCORRERT WAY] Find the details of the student having the highest percentage in each branch
SELECT
	sname,
	branch,
	MAX(PER)
FROM trainees
group by branch
having branch like '%e';

-- Above statement is incorrect for SQLServer or postgres
-- Reason:
-- You are selecting sname, but sname is not in the GROUP BY clause and is not inside an aggregate function.
-- SQL does not know which sname to pick for each group, since there could be many names in the same branch.

-- What Actually Happens
-- Standard SQL: This query will fail with an error like:
-- column "sname" must appear in the GROUP BY clause or be used in an aggregate function
-- Some SQL dialects (like MySQL with ONLY_FULL_GROUP_BY off): It may pick a random sname from the group, but this is not reliable or standard.
--  - was getting percentage as 99 for student (Aman) who has perc 65.

-- [CORRECT WAY] Find topper(s) from each branch. For single one partition inside maxes to find rollno. Join with trainees with rollno since it PK.
SELECT t.sname, t.rollno, t.branch, t.per
FROM trainees t
INNER JOIN (
    SELECT branch, MAX(per) AS max_per
    FROM trainees
    WHERE branch LIKE '%e' -- can be added after on clause below too
    GROUP BY branch
) maxes
ON t.branch = maxes.branch AND t.per = maxes.max_per

-- [CORRECT WAY] Find unique topper from each branch despite having same percentage
SELECT t.sname, t.rollno, t.branch, t.per
FROM trainees t
INNER JOIN (
    SELECT
    	rollno,
    	ROW_NUMBER() OVER (PARTITION BY branch ORDER BY per DESC) AS rn
    FROM trainees
    WHERE branch LIKE '%e'
) topperPerBranch
ON t.rollno = topperPerBranch.rollno
where topperPerBranch.rn = 1

-- Find distinct branches
SELECT DISTINCT branch
FROM trainees;

-- Delete a specific record
DELETE FROM trainees
WHERE rollno = 105;

-- Delete all records from table
DELETE FROM trainees;

-- Drop the table itself
DROP TABLE trainees;

-- MySQL in XAMPP is actually MariaDB (a fork of MySQL)
-- Current version of MariaDB in XAMPP - 10.4.13-MariaDB
-- It is advised to not have different server for MySQL and MariaDB due to changes in connection strings and configs.
-- MariaDB is largely compatible with MySQL's APIs and commands, meaning you can generally use MySQL connectors to connect to it.
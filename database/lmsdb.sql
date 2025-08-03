-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 03, 2025 at 12:49 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lmsdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `actions`
--

CREATE TABLE `actions` (
  `action_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_id_perform` int(11) NOT NULL,
  `action_type` varchar(255) NOT NULL,
  `action_date` datetime NOT NULL,
  `action_details` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `author`
--

CREATE TABLE `author` (
  `author_id` int(11) NOT NULL,
  `author_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `author`
--

INSERT INTO `author` (`author_id`, `author_name`) VALUES
(1, 'andy miguel'),
(2, 'nice stella'),
(3, 'Wilson Godfrey'),
(4, 'Ferdinand Niragira'),
(5, 'Andy Miguel Habyarimana'),
(6, 'Obama Vela'),
(7, 'Vincent Biiruta'),
(8, 'Vincent Biruta');

-- --------------------------------------------------------

--
-- Table structure for table `authoring`
--

CREATE TABLE `authoring` (
  `author_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `contribution_type` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `authoring`
--

INSERT INTO `authoring` (`author_id`, `book_id`, `contribution_type`) VALUES
(3, 2, 'AUTHOR'),
(4, 3, 'AUTHOR'),
(5, 3, 'AUTHOR'),
(6, 4, 'AUTHOR'),
(8, 5, 'AUTHOR');

-- --------------------------------------------------------

--
-- Table structure for table `books`
--

CREATE TABLE `books` (
  `book_id` int(11) NOT NULL,
  `cat_id` int(11) NOT NULL,
  `book_title` varchar(255) NOT NULL,
  `book_pages` int(11) NOT NULL,
  `book_pub_year` varchar(4) NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`book_id`, `cat_id`, `book_title`, `book_pages`, `book_pub_year`, `status`) VALUES
(2, 2, 'iBT TOEFL', 500, '2014', 'RESERVED'),
(3, 3, 'Linux Administration', 418, '2021', 'ISSUED'),
(4, 4, 'L\'age de Grace', 238, '2001', 'AVAILABLE'),
(5, 1, '100 Days', 273, '1996', 'ISSUED');

-- --------------------------------------------------------

--
-- Table structure for table `book_reservations`
--

CREATE TABLE `book_reservations` (
  `reservation_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `reservation_date` datetime NOT NULL DEFAULT current_timestamp(),
  `expiry_date` datetime NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Book reservations table';

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `cat_id` int(11) NOT NULL,
  `cat_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`cat_id`, `cat_name`) VALUES
(1, 'History'),
(2, 'Language'),
(3, 'Computer Science'),
(4, 'Science Fiction'),
(5, 'grg'),
(6, 'vdsvdsv');

-- --------------------------------------------------------

--
-- Table structure for table `loans`
--

CREATE TABLE `loans` (
  `loan_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `user_id_issue` int(11) NOT NULL,
  `loan_issue_date` date NOT NULL,
  `loan_return_date` date NOT NULL,
  `returned` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `loans`
--

INSERT INTO `loans` (`loan_id`, `user_id`, `book_id`, `user_id_issue`, `loan_issue_date`, `loan_return_date`, `returned`) VALUES
(1, 6, 3, 12, '2025-08-02', '2025-08-16', 1),
(2, 5, 5, 2, '2025-07-15', '2025-08-01', 0),
(3, 6, 3, 2, '2025-07-23', '2025-08-15', 0),
(4, 16, 5, 14, '2025-07-29', '2025-08-17', 0),
(5, 16, 2, 14, '2025-07-14', '2025-07-28', 0),
(6, 16, 4, 14, '2025-07-22', '2025-08-05', 0),
(7, 16, 3, 14, '2025-07-04', '2025-07-18', 1),
(8, 17, 5, 13, '2025-08-03', '2025-08-17', 0);

-- --------------------------------------------------------

--
-- Table structure for table `reserve`
--

CREATE TABLE `reserve` (
  `seat_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `reserve_due` datetime NOT NULL,
  `reserve_end` datetime NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `seats`
--

CREATE TABLE `seats` (
  `seat_id` int(11) NOT NULL,
  `seat_location` varchar(255) NOT NULL,
  `seat_type` varchar(255) NOT NULL,
  `seat_status` varchar(255) NOT NULL DEFAULT 'AVAILABLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seats`
--

INSERT INTO `seats` (`seat_id`, `seat_location`, `seat_type`, `seat_status`) VALUES
(1, 'A-101', 'STUDY', 'AVAILABLE'),
(2, 'A-102', 'STUDY', 'AVAILABLE'),
(3, 'A-103', 'STUDY', 'AVAILABLE'),
(4, 'A-104', 'STUDY', 'AVAILABLE'),
(5, 'A-105', 'STUDY', 'AVAILABLE'),
(6, 'B-201', 'COMPUTER', 'AVAILABLE'),
(7, 'B-202', 'COMPUTER', 'AVAILABLE'),
(8, 'B-203', 'COMPUTER', 'AVAILABLE'),
(9, 'B-204', 'COMPUTER', 'AVAILABLE'),
(10, 'B-205', 'COMPUTER', 'AVAILABLE'),
(11, 'C-301', 'GROUP', 'AVAILABLE'),
(12, 'C-302', 'GROUP', 'AVAILABLE'),
(13, 'C-303', 'GROUP', 'AVAILABLE'),
(14, 'D-401', 'SILENT', 'AVAILABLE'),
(15, 'D-402', 'SILENT', 'AVAILABLE'),
(16, 'D-403', 'SILENT', 'AVAILABLE'),
(17, 'D-404', 'SILENT', 'AVAILABLE'),
(18, 'D-405', 'SILENT', 'AVAILABLE'),
(19, 'E-501', 'STUDY', 'MAINTENANCE'),
(20, 'E-502', 'STUDY', 'AVAILABLE');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `user_fname` varchar(255) NOT NULL,
  `user_lname` varchar(255) NOT NULL,
  `user_sex` char(32) NOT NULL,
  `user_phone` varchar(255) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_pwd` varchar(255) NOT NULL,
  `user_role` varchar(255) NOT NULL,
  `user_status` varchar(255) NOT NULL,
  `createdat` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `user_fname`, `user_lname`, `user_sex`, `user_phone`, `user_email`, `user_pwd`, `user_role`, `user_status`, `createdat`) VALUES
(13, 'System', 'Admin', 'M', '0276451937', 'admin@lms.lib', '9552e277ebcc7fa191292c6e900d94dfe6e837d8abf76a2da927d4530d2c8f69', 'ADMIN', 'ACTIVE', '2025-08-03 00:46:50'),
(14, 'John', 'Ray', 'M', '0987654321', 'lib@library.com', '6518454a49ab2912238b510b2221f0fc1ce404986d3fb94bb34311ff6069d467', 'LIBRARIAN', 'ACTIVE', '2025-08-03 00:46:50'),
(15, 'Jane', 'Foster', 'F', '1122334455', 'member@library.com', '02ac4dd1e569f38c11a556fa1638a33dff521b56453a137710eb9a870762d153', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(16, 'Alice', 'Smith', 'Female', '5551000000', 'alice.smith@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'INACTIVE', '2025-08-03 00:46:51'),
(17, 'Bob', 'Johnson', 'Male', '5551000001', 'bob.johnson@yahoo.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(18, 'Charlie', 'Williams', 'Female', '5551000002', 'charlie.williams@hotmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(19, 'Diana', 'Brown', 'Male', '5551000003', 'diana.brown@outlook.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(20, 'Edward', 'Jones', 'Female', '5551000004', 'edward.jones@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(21, 'Fiona', 'Garcia', 'Male', '5551000005', 'fiona.garcia@yahoo.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'INACTIVE', '2025-08-03 00:46:51'),
(22, 'George', 'Miller', 'Female', '5551000006', 'george.miller@hotmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51'),
(23, 'Helen', 'Davis', 'Male', '5551000007', 'helen.davis@outlook.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-08-03 00:46:51');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `actions`
--
ALTER TABLE `actions`
  ADD PRIMARY KEY (`action_id`),
  ADD KEY `fk_actions_books` (`book_id`),
  ADD KEY `fk_actions_users` (`user_id`),
  ADD KEY `fk_actions_users1` (`user_id_perform`);

--
-- Indexes for table `author`
--
ALTER TABLE `author`
  ADD PRIMARY KEY (`author_id`);

--
-- Indexes for table `authoring`
--
ALTER TABLE `authoring`
  ADD PRIMARY KEY (`author_id`,`book_id`),
  ADD KEY `fk_authoring_books` (`book_id`);

--
-- Indexes for table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`book_id`),
  ADD KEY `fk_books_categories` (`cat_id`);

--
-- Indexes for table `book_reservations`
--
ALTER TABLE `book_reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_book_id` (`book_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_expiry_date` (`expiry_date`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`cat_id`);

--
-- Indexes for table `loans`
--
ALTER TABLE `loans`
  ADD PRIMARY KEY (`loan_id`),
  ADD KEY `fk_loans_users` (`user_id`),
  ADD KEY `fk_loans_books` (`book_id`),
  ADD KEY `fk_loans_users1` (`user_id_issue`);

--
-- Indexes for table `reserve`
--
ALTER TABLE `reserve`
  ADD PRIMARY KEY (`seat_id`,`user_id`),
  ADD KEY `fk_reserve_users` (`user_id`);

--
-- Indexes for table `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`seat_id`),
  ADD KEY `idx_seat_status` (`seat_status`),
  ADD KEY `idx_seat_type` (`seat_type`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `actions`
--
ALTER TABLE `actions`
  MODIFY `action_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `author`
--
ALTER TABLE `author`
  MODIFY `author_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `books`
--
ALTER TABLE `books`
  MODIFY `book_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `book_reservations`
--
ALTER TABLE `book_reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `cat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `loans`
--
ALTER TABLE `loans`
  MODIFY `loan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `seats`
--
ALTER TABLE `seats`
  MODIFY `seat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `actions`
--
ALTER TABLE `actions`
  ADD CONSTRAINT `fk_actions_books` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  ADD CONSTRAINT `fk_actions_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_actions_users1` FOREIGN KEY (`user_id_perform`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `authoring`
--
ALTER TABLE `authoring`
  ADD CONSTRAINT `fk_authoring_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`),
  ADD CONSTRAINT `fk_authoring_books` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`);

--
-- Constraints for table `books`
--
ALTER TABLE `books`
  ADD CONSTRAINT `fk_books_categories` FOREIGN KEY (`cat_id`) REFERENCES `categories` (`cat_id`);

--
-- Constraints for table `book_reservations`
--
ALTER TABLE `book_reservations`
  ADD CONSTRAINT `book_reservations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `book_reservations_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE;

--
-- Constraints for table `loans`
--
ALTER TABLE `loans`
  ADD CONSTRAINT `fk_loans_books` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  ADD CONSTRAINT `fk_loans_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_loans_users1` FOREIGN KEY (`user_id_issue`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `reserve`
--
ALTER TABLE `reserve`
  ADD CONSTRAINT `fk_reserve_seats` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`),
  ADD CONSTRAINT `fk_reserve_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

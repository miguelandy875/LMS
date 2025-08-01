-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2025 at 04:09 AM
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
(1, 'Andy Miguel', 'Habyarimana', 'M', '1234567890', 'miguel@library.com', '1372623a4c1f75b56a2974c39d6d21f18ef8af80dbf61b18234f03811c6938cb', 'ADMIN', 'ACTIVE', '2025-07-31 06:26:48'),
(2, 'John', 'Ray', 'M', '0987654321', 'lib@library.com', '6518454a49ab2912238b510b2221f0fc1ce404986d3fb94bb34311ff6069d467', 'LIBRARIAN', 'ACTIVE', '2025-07-31 06:26:48'),
(3, 'Jane', 'Foster', 'F', '1122334455', 'member@library.com', '02ac4dd1e569f38c11a556fa1638a33dff521b56453a137710eb9a870762d153', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(4, 'Alice', 'Smith', 'Female', '5551000000', 'alice.smith@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'INACTIVE', '2025-07-31 06:26:49'),
(5, 'Bob', 'Johnson', 'Male', '5551000001', 'bob.johnson@yahoo.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(6, 'Charlie', 'Williams', 'Female', '5551000002', 'charlie.williams@hotmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(7, 'Diana', 'Brown', 'Male', '5551000003', 'diana.brown@outlook.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(8, 'Edward', 'Jones', 'Female', '5551000004', 'edward.jones@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(9, 'Fiona', 'Garcia', 'Male', '5551000005', 'fiona.garcia@yahoo.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'INACTIVE', '2025-07-31 06:26:49'),
(10, 'George', 'Miller', 'Female', '5551000006', 'george.miller@hotmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49'),
(11, 'Helen', 'Davis', 'Male', '5551000007', 'helen.davis@outlook.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MEMBER', 'ACTIVE', '2025-07-31 06:26:49');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

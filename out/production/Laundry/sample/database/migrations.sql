SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laundry`
--
DROP DATABASE laundry;
CREATE DATABASE laundry;
USE laundry;

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE `inventory` (
                             `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                             `supply_id` int(30) NOT NULL,
                             `qty` int(30) NOT NULL,
                             `stock_type` tinyint(1) NOT NULL COMMENT '1= in, 2 = used',
                             `date_created` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`id`, `supply_id`, `qty`, `stock_type`, `date_created`) VALUES
(1, 1, 20, 1, '2020-09-23 14:08:04'),
(2, 2, 10, 1, '2020-09-23 14:08:14'),
(3, 3, 20, 1, '2020-09-23 14:09:29');

-- --------------------------------------------------------

--
-- Table structure for table `laundry_categories`
--

CREATE TABLE `categories` (
                              `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                              `title` text NOT NULL,
                              `price` FLOAT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `laundry_categories`
--

INSERT INTO `categories` (`id`, title, `price`) VALUES
(1, 'Bed Sheets', 30),
(3, 'Clothes', 25);

-- --------------------------------------------------------

--
-- Table structure for table `laundry_list`
--
DROP TABLE IF EXISTS laundry_list;
CREATE TABLE `laundry_list` (
                                `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                                `customer_name` text NOT NULL,
                                `status` VARCHAR(20) NOT NULL DEFAULT ('pending') COMMENT '0 = Pending, 1 = Ongoing, 2 = Ready, 3 = Claimed',
                                `total` FLOAT NOT NULL,
                                `paid` tinyint(1) DEFAULT 0,
                                `amount_paid` FLOAT NULL,
                                `amount_change` FLOAT NULL,
                                `remarks` text DEFAULT ('none'),
                                `date_created` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `laundry_list`
--

INSERT INTO `laundry_list` (`id`, `customer_name`, `status`, `total`, `paid`, `amount_paid`, `amount_change`, `remarks`, `date_created`) VALUES
(2, 'James Smith', 'claimed', 555, 1, 555, 0, 'none', '2020-09-23 11:54:47'),
(4, 'Claire Blake', 'claimed', 250, 1, 500, 250, 'none', '2020-09-23 13:29:33');

-- --------------------------------------------------------

--
-- Table structure for table `laundry_items`
--
DROP TABLE IF EXISTS laundry_items;
CREATE TABLE `laundry_items` (
                                 `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                                 `laundry_id` int(30) NOT NULL,
                                 `category_id` int(30) NOT NULL,
                                 `weight` FLOAT NOT NULL,
                                 `unit_price` FLOAT NOT NULL,
                                 FOREIGN KEY (laundry_id) REFERENCES laundry_list(id) ON DELETE CASCADE,
                                 FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `laundry_items`
--

INSERT INTO `laundry_items` (`id`, category_id, `weight`, `laundry_id`, `unit_price`) VALUES
(4, 3, 10, 4, 25);

-- --------------------------------------------------------

--
-- Table structure for table `supply_list`
--

CREATE TABLE `supply_list` (
                               `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                               `name` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `supply_list`
--

INSERT INTO `supply_list` (`id`, `name`) VALUES
(1, 'Fabric Detergent'),
(2, 'Fabric Conditioner'),
(3, 'Baking Soda');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--
DROP TABLE users;
CREATE TABLE `users` (
                         `id` int(30) AUTO_INCREMENT PRIMARY KEY,
                         `name` varchar(200) NOT NULL,
                         `username` varchar(100) NOT NULL UNIQUE,
                         `phone` integer UNIQUE,
                         `password` varchar(200) NOT NULL,
                         `type` boolean NOT NULL DEFAULT false COMMENT '1 = Admin, 0 = Staff'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `username`, `password`, `type`) VALUES
(1, 'Administrator', 'admin', 'admin123', true),
(4, 'John Smith', 'jsmith', 'admin123', false);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
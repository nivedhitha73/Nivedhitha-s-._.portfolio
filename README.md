# Bite Box: food ordering app (Java, JDBC, MVC)

Browse a menu, open item details, add to cart, check out, view recent orders, re-order or cancel. Light and dark mode included.

## Features
- Menu with category filter and search (data comes from the database)
- Item details page, with a "Recently viewed" list (kept per browser session)
- Cart: add, change quantity, remove, empty
- Checkout: name, phone, kitchen note; order saved in a single JDBC transaction
- Recent orders, order detail, "Order again", cancel (while status is PLACED)

## MVC layout
- Model:       `model/MenuItem`, `OrderLine`, `Order`
- View:        `WEB-INF/views/*.jsp`, `css/style.css`, `js/theme.js`
- Controller:  `MenuController` (/menu, /item), `CartController` (/cart), `OrderController` (/orders)
- JDBC / DAO:  `DBUtil` (connection, tables, sample menu), `MenuDAO`, `OrderDAO`

## Database
Embedded H2 file database at `~/bitebox_db` (created on first run, tables and 12 sample dishes included).
Tables: `menu_items`, `orders`, `order_items`.
To use MySQL: edit URL/USER/PASS in `DBUtil.java` and swap the H2 dependency in `pom.xml` for `mysql-connector-j`.

## Run
Needs JDK 11+ and Maven 3.6+.

    mvn tomcat7:run

Open http://localhost:8080
Or run `mvn package` and deploy `target/bite-box.war` to Tomcat 8/9.

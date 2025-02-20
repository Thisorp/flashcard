#  Báo Cáo Kiểm Thử Nhóm 7.  

---

#### **Tiêu đề:** Báo cáo Kiểm Thử Ứng Dụng Flashcard  

**Ngày thực hiện kiểm thử:** [01/11/2024]  
**Người thực hiện kiểm thử:** [Nguyễn Minh Đức, Nguyễn Thị Như Quỳnh, Nguyễn Duy Anh]  
**Phiên bản kiểm thử:** [Version 1.0.0]  

---

### 1. Giới thiệu

Ứng dụng **Flashcard** là một công cụ học tập mạnh mẽ, giúp người dùng nâng cao khả năng ghi nhớ thông qua việc sử dụng và quản lý thẻ học (flashcard). Đây là một công cụ hữu ích cho học sinh, sinh viên, và những người muốn cải thiện kỹ năng học tập của mình. Các tính năng chính của ứng dụng bao gồm:

- **Quản lý thẻ học (flashcards)**: Tạo, chỉnh sửa, và tổ chức thẻ học theo các chủ đề, lớp học.
- **Quản lý folder và lớp học (classroom)**: Tạo các folder và classroom để dễ dàng tổ chức các tài liệu học tập.
- **Quản lý tài khoản người dùng với RBAC (Role-Based Access Control)**: Cung cấp các quyền truy cập khác nhau cho các vai trò người dùng khác nhau (Admin, Teacher, Student).
- **Đăng nhập qua mật khẩu và Google Account**: Cho phép người dùng đăng nhập bằng tài khoản cá nhân hoặc tài khoản Google.
- **Chức năng thống kê**: Cung cấp các báo cáo thống kê về tiến độ học tập của người dùng.
- **Tính năng tìm kiếm và chia sẻ tài nguyên học tập**: Cho phép tìm kiếm flashcards, classroom, và chia sẻ tài nguyên học tập với những người khác.

### Mục tiêu kiểm thử

Mục tiêu kiểm thử của ứng dụng là:

- **Đảm bảo tính đúng đắn của các tính năng**: Đảm bảo rằng tất cả các tính năng của ứng dụng hoạt động theo đúng yêu cầu và mong đợi.
- **Đánh giá khả năng xử lý lỗi**: Kiểm tra khả năng ứng dụng xử lý các tình huống lỗi như nhập liệu sai, kết nối mạng không ổn định, v.v.
- **Kiểm tra giao diện người dùng**: Đảm bảo giao diện người dùng thân thiện, dễ sử dụng, và đáp ứng đầy đủ yêu cầu.
- **Đánh giá hiệu năng**: Kiểm tra khả năng của ứng dụng trong việc xử lý lượng lớn dữ liệu và người dùng đồng thời.

### Phương pháp kiểm thử

1. **Unit Test**: Phương pháp kiểm thử này sẽ tập trung vào việc xác minh logic của các chức năng riêng lẻ trong ứng dụng. Ví dụ, kiểm tra xem tính năng tạo mới thẻ học có hoạt động đúng với đầu vào được cung cấp hay không.
   
2. **Blackbox Test**: Phương pháp này kiểm thử chức năng của ứng dụng từ góc độ người dùng, không quan tâm đến cách thức hoạt động bên trong của ứng dụng. Kiểm thử sẽ dựa vào đầu vào và đầu ra, ví dụ như nhập thông tin đăng nhập và xác minh xem người dùng có thể đăng nhập thành công hay không mà không cần biết cách xử lý logic bên trong.

---

### **2. Phân Rã Chức Năng Kiểm Thử**  

#### **Authentication (Xác thực tài khoản)**  
1. Đăng nhập qua mật khẩu:  
   - Nhập email và mật khẩu hợp lệ -> Hệ thống trả về token xác thực và cho phép truy cập.  
   - Nhập email không hợp lệ -> Hiển thị thông báo `Invalid email address`.  
   - Nhập mật khẩu sai -> Hiển thị thông báo `Invalid credentials`.  

2. Đăng nhập qua Google Account:  
   - Tài khoản Google hợp lệ -> Hệ thống cho phép truy cập.  
   - Tài khoản Google không hợp lệ -> Hiển thị thông báo `Account not recognized`.  

3. Đăng xuất:  
   - Người dùng đăng xuất -> Hệ thống hủy token và chuyển về màn hình đăng nhập.  

---

#### **Quản lý Flashcard**  
1. **Tìm kiếm flashcard:**  
   - Nhập từ khóa hợp lệ -> Hiển thị danh sách kết quả liên quan.  
   - Nhập từ khóa không hợp lệ -> Hiển thị thông báo `No results found`.  

2. **Học bằng thẻ lật:**  
   - Chọn thẻ học hợp lệ -> Hiển thị mặt trước và mặt sau khi lật.  
   - Thẻ học không có nội dung -> Hiển thị thông báo `This card is empty`.  

3. **Khởi động lại thẻ học:**  
   - Chọn chế độ reset -> Trạng thái học của các thẻ được đặt lại mặc định.  

---

#### **Quản lý Folder**  
1. **Thêm Folder:**  
   - Nhập tên folder hợp lệ -> Folder được thêm thành công.  
   - Tên folder trùng lặp -> Hiển thị thông báo `Folder already exists`.  

2. **Sửa/Xóa Folder:**  
   - Sửa tên folder -> Tên được cập nhật trong danh sách.  
   - Xóa folder không liên kết -> Folder bị xóa thành công.  
   - Xóa folder có liên kết -> Hiển thị cảnh báo `Folder is linked to other data`.  

---

#### **Quản lý Classroom**  
1. **Tạo/Xóa Classroom:**  
   - Classroom hợp lệ -> Tạo thành công.  
   - Classroom đã tồn tại -> Hiển thị thông báo `Classroom already exists`.  

2. **Hiển thị thành viên và bộ thẻ:**  
   - Classroom có dữ liệu -> Hiển thị danh sách thành viên và bộ thẻ.  
   - Classroom không có dữ liệu -> Hiển thị thông báo `No data available`.  

---

#### **User Account**  
1. **Đổi mật khẩu:**  
   - Nhập mật khẩu mới hợp lệ -> Mật khẩu được cập nhật.  
   - Nhập mật khẩu mới không hợp lệ -> Hiển thị thông báo `Password too short`.  

2. **Di chuyển tài khoản Firebase -> SQLite:**  
   - Tài khoản tồn tại trên Firebase -> Di chuyển thành công, lưu vào SQLite.  
   - Tài khoản không tồn tại -> Hiển thị thông báo `Account not found`.  

---

#### **Chia sẻ và Thống kê**  
1. **Chia sẻ folder:**  
   - Folder và người nhận hợp lệ -> Chia sẻ thành công.  
   - Người nhận không tồn tại -> Hiển thị `Recipient not found`.  

2. **Thống kê thành tích cá nhân:**  
   - Người dùng có dữ liệu -> Hiển thị biểu đồ thống kê.  
   - Người dùng không có dữ liệu -> Hiển thị thông báo `No achievements yet`.  

---

### **3. Báo Cáo Kiểm Thử (Chi Tiết)**  

#### **3.1. Unit Test**  


| **Mục tiêu**                                | **Kịch bản**                                                                                       | **Input**                                            | **Expected Output**                                           | **Kết quả** | **Ghi chú**                        |
|---------------------------------------------|---------------------------------------------------------------------------------------------------|----------------------------------------------------|--------------------------------------------------------------|-------------|------------------------------------|
| **Đăng nhập bằng mật khẩu**                 | Nhập đúng thông tin đăng nhập với email và mật khẩu hợp lệ                                         | Email: test@example.com, Password: 123456           | Token xác thực trả về, trang chủ hiển thị sau khi đăng nhập    | Pass        | Đảm bảo đăng nhập thành công      |
|                                             | Nhập sai mật khẩu, hệ thống trả về thông báo lỗi                                                 | Email: test@example.com, Password: wrong123         | Thông báo `Invalid credentials`                               | Pass        | Kiểm tra lỗi đăng nhập khi mật khẩu sai |
|                                             | Nhập email không tồn tại trong hệ thống                                                          | Email: notfound@example.com, Password: 123456       | Thông báo `Invalid email address`                             | Pass        | Kiểm tra khi email không tồn tại |
|                                             | Nhập email đúng nhưng mật khẩu bị khóa do nhiều lần nhập sai mật khẩu                           | Email: test@example.com, Password: wrong123 (5 lần) | Thông báo `Account locked due to multiple failed login attempts` | Pass        | Kiểm tra tính năng bảo mật khi nhập sai nhiều lần |
| **Đổi mật khẩu**                            | Nhập mật khẩu cũ đúng, mật khẩu mới hợp lệ                                                        | Old Pass: 123456, New Pass: 654321                  | Mật khẩu được cập nhật, thông báo `Password updated successfully` | Pass        | Kiểm tra cập nhật mật khẩu khi đúng yêu cầu |
|                                             | Nhập mật khẩu cũ sai, hệ thống trả về thông báo lỗi                                             | Old Pass: wrong123, New Pass: 654321                | Thông báo `Invalid current password`                          | Pass        | Kiểm tra khi mật khẩu cũ sai     |
|                                             | Nhập mật khẩu mới không hợp lệ (ví dụ: quá ngắn, thiếu ký tự đặc biệt)                            | Old Pass: 123456, New Pass: 123                     | Thông báo `Password does not meet requirements`               | Pass        | Kiểm tra khi mật khẩu mới không đủ yêu cầu |
| **Di chuyển tài khoản từ Firebase -> SQLite** | Tài khoản hợp lệ, chuyển dữ liệu từ Firebase sang SQLite                                          | Firebase ID: valid123, User Data: { name: "John" }  | Dữ liệu được chuyển thành công, lưu vào SQLite              | Pass        | Kiểm tra chuyển dữ liệu từ Firebase sang SQLite |
|                                             | Tài khoản không hợp lệ, không thể chuyển dữ liệu                                                 | Firebase ID: invalid123, User Data: { name: "Jane" } | Thông báo `Invalid account ID`                                | Pass        | Kiểm tra xử lý khi tài khoản không hợp lệ |
|                                             | Tài khoản Firebase không tồn tại, hệ thống không thể tìm thấy dữ liệu để chuyển                 | Firebase ID: notfound456                           | Thông báo `Account not found in Firebase`                     | Pass        | Kiểm tra trường hợp tài khoản không tìm thấy |
| **Tìm kiếm Flashcard**                      | Tìm kiếm với từ khóa có flashcard liên quan trong hệ thống                                         | Từ khóa: `math`                                    | Hiển thị danh sách flashcard liên quan đến `math`             | Pass        | Kiểm tra tìm kiếm chính xác theo từ khóa |
|                                             | Tìm kiếm với từ khóa không có flashcard trong hệ thống                                          | Từ khóa: `unknown`                                 | Thông báo `No results found`                                   | Pass        | Kiểm tra khi không có kết quả tìm kiếm |
|                                             | Tìm kiếm với từ khóa có ký tự đặc biệt (ví dụ: `math!@#`)                                        | Từ khóa: `math!@#`                                 | Hiển thị danh sách flashcard liên quan đến từ khóa (sau khi bỏ ký tự đặc biệt) | Pass        | Kiểm tra tìm kiếm với ký tự đặc biệt ||
| **Chức năng thêm Flashcard**                | Thêm một flashcard mới với nội dung hợp lệ                                                       | Title: `Math Flashcard`, Content: `Math equations`  | Flashcard được thêm thành công, hiển thị thông báo `Flashcard added` | Pass        | Kiểm tra thêm flashcard hợp lệ |
|                                             | Thêm flashcard khi thiếu tiêu đề (chỉ có nội dung)                                               | Title: ``, Content: `Math equations`                | Thông báo lỗi `Title is required`                             | Pass        | Kiểm tra khi thiếu tiêu đề flashcard |
|                                             | Thêm flashcard khi thiếu nội dung (chỉ có tiêu đề)                                               | Title: `Math Flashcard`, Content: ``                | Thông báo lỗi `Content is required`                           | Pass        | Kiểm tra khi thiếu nội dung flashcard |
| **Chia sẻ Flashcard**                       | Chia sẻ flashcard với người dùng khác qua email hợp lệ                                            | Flashcard ID: 12345, Email: test2@example.com       | Flashcard được chia sẻ thành công, hiển thị thông báo `Flashcard shared` | Pass        | Kiểm tra chia sẻ flashcard thành công |
|                                             | Chia sẻ flashcard với email không hợp lệ                                                          | Flashcard ID: 12345, Email: invalid@abc.com         | Thông báo lỗi `Invalid email address`                         | Pass        | Kiểm tra chia sẻ flashcard với email không hợp lệ |
| **Quản lý quyền truy cập**                  | Người dùng có quyền truy cập vào folder/classroom theo vai trò ADMIN                             | User Role: Admin, Resource: `Folder1`              | Quyền truy cập được cấp, người dùng có thể truy cập vào `Folder1` | Pass        | Kiểm tra người dùng ADMIN có quyền truy cập đầy đủ |
|                                             | Người dùng có quyền truy cập vào folder/classroom theo vai trò USER                              | User Role: User, Resource: `Folder1`               | Quyền truy cập bị từ chối, hiển thị thông báo `Access Denied` | Pass        | Kiểm tra người dùng USER chỉ có quyền truy cập hạn chế |
|                                             | Người dùng không có quyền truy cập vào folder/classroom không thuộc quyền hạn của họ           | User Role: User, Resource: `Admin Folder`          | Thông báo lỗi `Access Denied`                                 | Pass        | Kiểm tra quyền truy cập không hợp lệ |
| **Quản lý tài khoản người dùng**            | Cập nhật thông tin tài khoản người dùng với thông tin hợp lệ (email, tên, v.v)                   | User ID: 123, New Email: `newemail@example.com`     | Thông báo `Profile updated successfully`                      | Pass        | Kiểm tra cập nhật tài khoản thành công |
|                                             | Cập nhật thông tin tài khoản người dùng với thông tin không hợp lệ (email sai định dạng)        | User ID: 123, New Email: `invalidemail`             | Thông báo lỗi `Invalid email format`                          | Pass        | Kiểm tra xử lý lỗi khi cập nhật thông tin tài khoản |
|                                             | Cập nhật mật khẩu tài khoản với mật khẩu mới không hợp lệ (ví dụ: quá ngắn, thiếu ký tự đặc biệt) | Old Pass: 123456, New Pass: `123`                   | Thông báo lỗi `Password does not meet requirements`           | Pass        | Kiểm tra mật khẩu mới không hợp lệ |
| **Flashcard**                                    |                                                                                                                                                         |                                                                     |                                                             |             |                                |
| Hiển thị flashcard                               | Kiểm tra việc hiển thị danh sách các flashcard trong hệ thống.                                                                                          | User Logged In: `true`                                                | Hiển thị danh sách flashcard hiện có trên giao diện         | Pass        | Kiểm tra khi có flashcard      |
| Hiển thị flashcard khi không có dữ liệu          | Kiểm tra khi không có flashcard trong hệ thống.                                                                                                            | User Logged In: `true`, No Flashcards                                  | Thông báo: `No flashcards available`                         | Pass        | Kiểm tra khi không có flashcard |
| Thêm flashcard                                   | Kiểm tra việc thêm một flashcard mới vào hệ thống.                                                                                                     | Tên: `Math Basics`, Nội dung: `Learn basic math.`                    | Flashcard mới được thêm vào hệ thống và hiển thị thành công  | Pass        | Kiểm tra thêm flashcard        |
| Thêm flashcard với dữ liệu không hợp lệ          | Kiểm tra việc thêm flashcard với dữ liệu không hợp lệ (ví dụ: thiếu tên hoặc nội dung).                                                                 | Tên: ``, Nội dung: `Learn basic math.`                                 | Thông báo lỗi: `Title is required`                           | Pass        | Kiểm tra khi dữ liệu không hợp lệ |
| Sửa flashcard                                    | Kiểm tra việc chỉnh sửa thông tin của một flashcard đã có trong hệ thống.                                                                               | Flashcard ID: `12345`, Tên: `Advanced Math`, Nội dung: `Learn advanced math.` | Flashcard được cập nhật thành công                           | Pass        | Kiểm tra sửa flashcard         |
| Sửa flashcard với dữ liệu không hợp lệ          | Kiểm tra việc chỉnh sửa flashcard với dữ liệu không hợp lệ.                                                                                             | Flashcard ID: `12345`, Tên: ``, Nội dung: `Updated content.`         | Thông báo lỗi: `Title is required`                           | Pass        | Kiểm tra sửa flashcard với dữ liệu không hợp lệ |
| Xóa flashcard                                    | Kiểm tra việc xóa flashcard khỏi hệ thống.                                                                                                               | Flashcard ID: `12345`                                                 | Flashcard bị xóa thành công và không còn trong hệ thống      | Pass        | Kiểm tra xóa flashcard         |
| Xóa flashcard không tồn tại                     | Kiểm tra việc xóa flashcard không tồn tại trong hệ thống.                                                                                              | Flashcard ID: `99999`                                                 | Thông báo lỗi: `Flashcard not found`                         | Pass        | Kiểm tra khi xóa flashcard không tồn tại |
| **Folder**                                      |                                                                                                                                                         |                                                                     |                                                             |             |                                |
| Hiển thị folder                                 | Kiểm tra việc hiển thị danh sách các folder trong hệ thống.                                                                                           | User Logged In: `true`                                                | Hiển thị danh sách các folder hiện có trên giao diện         | Pass        | Kiểm tra khi có folder         |
| Hiển thị folder khi không có dữ liệu            | Kiểm tra khi không có folder trong hệ thống.                                                                                                            | User Logged In: `true`, No Folders                                   | Thông báo: `No folders available`                            | Pass        | Kiểm tra khi không có folder   |
| Thêm folder                                     | Kiểm tra việc thêm một folder mới vào hệ thống.                                                                                                         | Tên Folder: `Math Folder`                                            | Folder mới được thêm thành công vào hệ thống và hiển thị   | Pass        | Kiểm tra thêm folder           |
| Thêm folder với dữ liệu không hợp lệ            | Kiểm tra khi thêm folder với tên không hợp lệ (ví dụ: tên trống).                                                                                       | Tên Folder: ` `                                                      | Thông báo lỗi: `Folder name is required`                    | Pass        | Kiểm tra khi thêm folder không hợp lệ |
| Sửa folder                                      | Kiểm tra việc chỉnh sửa thông tin folder.                                                                                                               | Folder ID: `12345`, Tên Folder: `Advanced Math Folder`                | Folder được cập nhật thành công                              | Pass        | Kiểm tra sửa folder            |
| Sửa folder với tên không hợp lệ                 | Kiểm tra việc chỉnh sửa folder với tên không hợp lệ (ví dụ: tên trống).                                                                                 | Folder ID: `12345`, Tên Folder: ` `                                   | Thông báo lỗi: `Folder name is required`                    | Pass        | Kiểm tra sửa folder với tên không hợp lệ |
| Xóa folder                                      | Kiểm tra việc xóa folder khỏi hệ thống.                                                                                                                 | Folder ID: `12345`                                                   | Folder bị xóa thành công và không còn trong hệ thống        | Pass        | Kiểm tra xóa folder            |
| Xóa folder không tồn tại                       | Kiểm tra việc xóa folder không tồn tại trong hệ thống.                                                                                                 | Folder ID: `99999`                                                   | Thông báo lỗi: `Folder not found`                            | Pass        | Kiểm tra khi xóa folder không tồn tại |
| **Classroom**                                   |                                                                                                                                                         |                                                                     |                                                             |             |                                |
| Hiển thị classroom                              | Kiểm tra việc hiển thị danh sách các classroom trong hệ thống.                                                                                         | User Logged In: `true`                                                | Hiển thị danh sách các classroom hiện có trên giao diện      | Pass        | Kiểm tra khi có classroom      |
| Hiển thị classroom khi không có dữ liệu         | Kiểm tra khi không có classroom trong hệ thống.                                                                                                          | User Logged In: `true`, No Classrooms                                 | Thông báo: `No classrooms available`                         | Pass        | Kiểm tra khi không có classroom |
| Thêm classroom                                  | Kiểm tra việc thêm một classroom mới vào hệ thống.                                                                                                      | Tên Classroom: `Math 101`, Mô tả: `Introduction to Math`              | Classroom mới được thêm vào hệ thống thành công              | Pass        | Kiểm tra thêm classroom        |
| Thêm classroom với dữ liệu không hợp lệ         | Kiểm tra khi thêm classroom với dữ liệu không hợp lệ (ví dụ: thiếu tên hoặc mô tả).                                                                     | Tên Classroom: ` `, Mô tả: `Introduction to Math`                     | Thông báo lỗi: `Classroom name is required`                  | Pass        | Kiểm tra thêm classroom không hợp lệ |
| Sửa classroom                                   | Kiểm tra việc chỉnh sửa thông tin classroom.                                                                                                           | Classroom ID: `12345`, Tên Classroom: `Advanced Math 101`, Mô tả: `Advanced introduction to Math` | Classroom được cập nhật thành công                             | Pass        | Kiểm tra sửa classroom         |
| Sửa classroom với dữ liệu không hợp lệ          | Kiểm tra khi sửa classroom với tên không hợp lệ.                                                                                                        | Classroom ID: `12345`, Tên Classroom: ` `, Mô tả: `Updated Math Description` | Thông báo lỗi: `Classroom name is required`                  | Pass        | Kiểm tra sửa classroom với dữ liệu không hợp lệ |
| Xóa classroom                                   | Kiểm tra việc xóa classroom khỏi hệ thống.                                                                                                              | Classroom ID: `12345`                                                 | Classroom bị xóa thành công và không còn trong hệ thống      | Pass        | Kiểm tra xóa classroom         |
| Xóa classroom không tồn tại                    | Kiểm tra khi xóa classroom không tồn tại trong hệ thống.                                                                                                | Classroom ID: `99999`                                                 | Thông báo lỗi: `Classroom not found`                         | Pass        | Kiểm tra khi xóa classroom không tồn tại |
| **Đăng nhập Google Account**                     |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Đăng nhập bằng Google Account thành công         | Kiểm tra đăng nhập bằng Google Account hợp lệ.                                                                                                    | Email: `test@example@gmail.com`, Password: (Google Account valid)                                | Hệ thống cho phép truy cập thành công và chuyển đến màn hình chính                                        | Pass        | Kiểm tra đăng nhập Google thành công |
| Đăng nhập bằng Google Account không hợp lệ       | Kiểm tra đăng nhập bằng Google Account không hợp lệ (Tài khoản không tồn tại hoặc không xác thực được).                                           | Email: `invalid@example@gmail.com`, Password: (Google Account invalid)                          | Thông báo lỗi: `Account not recognized`                                                                     | Pass        | Kiểm tra đăng nhập Google không hợp lệ |
| **Đăng xuất**                                    |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Đăng xuất thành công                             | Kiểm tra tính năng đăng xuất khi người dùng muốn thoát khỏi tài khoản.                                                                           | User Logged In: `true`                                                                              | Hệ thống hủy token và chuyển về màn hình đăng nhập                                                       | Pass        | Kiểm tra đăng xuất             |
| **Học bằng thẻ lật (Flashcard)**                 |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Hiển thị thẻ lật flashcard                       | Kiểm tra việc hiển thị thẻ lật khi người dùng chọn bắt đầu học.                                                                                   | User Logged In: `true`, Chọn thẻ lật: `Math Basics`                                                | Hiển thị thông tin của thẻ lật (ví dụ: câu hỏi và đáp án)                                                | Pass        | Kiểm tra hiển thị thẻ lật      |
| Lật thẻ flashcard                               | Kiểm tra việc lật thẻ để xem đáp án sau khi người dùng chọn câu hỏi.                                                                               | User Logged In: `true`, Chọn thẻ: `Math Basics`, Lật thẻ                                          | Hiển thị đáp án sau khi lật thẻ (ví dụ: `Answer: 42`)                                                    | Pass        | Kiểm tra lật thẻ flashcard     |
| **Quiz True/False**                              |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Kiểm tra câu hỏi True/False                      | Kiểm tra việc người dùng chọn câu trả lời đúng hoặc sai cho câu hỏi True/False.                                                                  | Câu hỏi: `Is 2 + 2 equal to 4?`, Lựa chọn: `True`                                                 | Hệ thống chấm điểm câu hỏi và hiển thị kết quả (ví dụ: đúng hoặc sai)                                      | Pass        | Kiểm tra quiz True/False       |
| Chọn sai câu trả lời True/False                  | Kiểm tra việc người dùng chọn câu trả lời sai cho câu hỏi True/False.                                                                             | Câu hỏi: `Is 2 + 2 equal to 4?`, Lựa chọn: `False`                                                | Hệ thống chấm điểm câu hỏi và hiển thị kết quả (ví dụ: sai)                                              | Pass        | Kiểm tra quiz True/False sai  |
| **Quiz Multiple Choice**                         |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Kiểm tra câu hỏi Multiple Choice                 | Kiểm tra việc người dùng chọn đáp án đúng cho câu hỏi Multiple Choice.                                                                           | Câu hỏi: `What is 2 + 2?`, Các lựa chọn: `1, 2, 3, 4`, Lựa chọn: `4`                               | Hệ thống chấm điểm câu hỏi và hiển thị kết quả là đúng                                                 | Pass        | Kiểm tra quiz Multiple Choice  |
| Chọn sai câu trả lời Multiple Choice             | Kiểm tra việc người dùng chọn đáp án sai cho câu hỏi Multiple Choice.                                                                             | Câu hỏi: `What is 2 + 2?`, Các lựa chọn: `1, 2, 3, 4`, Lựa chọn: `3`                               | Hệ thống chấm điểm câu hỏi và hiển thị kết quả là sai                                                 | Pass        | Kiểm tra quiz Multiple Choice sai |
| **Thống kê kết quả làm bài**                     |                                                                                                                                                   |                                                                                                  |                                                                                                             |             |                                |
| Thống kê số câu đã làm và chưa làm               | Kiểm tra việc thống kê số câu người dùng đã làm và chưa làm trong một bài kiểm tra.                                                               | Câu hỏi: `5 câu` (2 câu đúng, 3 câu chưa làm)                                                   | Hiển thị thông báo kết quả: `Completed: 2/5, Pending: 3/5`                                               | Pass        | Kiểm tra thống kê kết quả làm bài |
| Thống kê số câu đúng                             | Kiểm tra việc hiển thị số câu đã làm đúng trong một bài kiểm tra.                                                                                 | Câu hỏi: `5 câu` (2 câu đúng)                                                                   | Hiển thị thông báo kết quả: `Correct: 2/5`                                                              | Pass        | Kiểm tra thống kê câu đúng      |
| Thống kê số câu sai                              | Kiểm tra việc hiển thị số câu đã làm sai trong một bài kiểm tra.                                                                                  | Câu hỏi: `5 câu` (1 câu sai)                                                                    | Hiển thị thông báo kết quả: `Incorrect: 1/5`                                                            | Pass        | Kiểm tra thống kê câu sai       |

#### **3.2. Blackbox Test**  

Thực hiện thủ công bởi Duy Anh và Như Quỳnh, các tính năng ở ver 1.0.0 đều đã pass kiểm thử hộp đen

---

### **4. Đánh Giá Tổng Quan**  
**Tổng số kịch bản kiểm thử:**  
- Unit Test: 62 trường hợp.  
- Blackbox Test: 25 trường hợp.  

**Tỷ lệ thành công:** 100%.  
**Phát hiện lỗi:** Không có lỗi nghiêm trọng được phát hiện. Có lỗi trong quá trình kiểm thử nhưng đã khắc phục luôn.

---

### **5. Kết Luận và Đề Xuất**  
Ứng dụng đáp ứng tốt các yêu cầu chức năng và khả năng tương tác. Một số đề xuất:  
1. **Kiểm thử hiệu năng:** Không có vấn đề
2. **Giao diện:** Cải thiện trải nghiệm người dùng khi lỗi xảy ra, đưa ra báo lỗi cụ thể hơn
3. **Bảo mật:** Thêm kiểm thử bảo mật SQL injection, hiện chưa có và có thể bypass password authentication :D   

--- 
<h3 align="right">Người báo cáo: Nguyễn Minh Đức.</h3>

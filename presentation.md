<h1 align="center">Đề tài đồ án: Ứng dụng Android Flashcard hỗ trợ học tập</h1>

---

### **1. Mục tiêu của đề tài:**

Trong cuộc sống hiện đại, việc học tập và làm việc đòi hỏi chúng ta phải tiếp thu và ghi nhớ một lượng lớn kiến thức. Tuy nhiên, không phải ai cũng có phương pháp học tập hiệu quả để ghi nhớ lâu dài. Chính vì vậy, bọn em chọn đề tài này nhằm ứng dụng kiến thức đã học vào thực tế, phục vụ nhu cầu ghi nhớ kiến thức hiệu quả, tạo môi trường học tập mở cho học sinh, sinh viên cũng như bất cứ ai có nhu cầu học tập 

Mục tiêu chính của bọn em là xây dựng một ứng dụng Android hỗ trợ học tập thông qua Flashcard - một ứng dụng Android dành cho học sinh, sinh viên và những người muốn học một cách hiệu quả. Ứng dụng được thiết kế nhằm tạo ra một không gian học tập linh hoạt, giúp người dùng quản lý tài liệu học tập qua hệ thống flashcard và folder, tổ chức nội dung học tập theo lớp học. Đồng thời, ứng dụng cung cấp các phương pháp kiểm tra và đánh giá tiến độ như chế độ học tập "learn by flashcard", kiểm tra kiến thức qua câu hỏi đúng sai dựa trên nội dung flashcard và câu hỏi trắc nghiệm đa đáp án. Hệ thống sẽ ghi nhận số câu trả lời đúng/sai và tính tỉ lệ kết quả bài làm. Các phương pháp trên giúp người học dễ nhớ hơn dựa trên cơ chế hồi tưởng chủ động, lặp lại ngắt quãng, hiệu ứng kiểm tra, và chunking. Việc chia nhỏ thông tin kích thích dopamine, củng cố các synapse (khớp thần kinh) giữa các neuron, tăng cường hoạt động ở hippocampus (trung tâm lưu trữ kí ức dài hạn) và vùng vỏ não trước trán, từ đó giúp ghi nhớ thông tin nhanh chóng và lâu dài.

Ngoài ra, ứng dụng sẽ tích hợp các chức năng quản lý tài khoản cá nhân, bao gồm đăng ký, đăng nhập, đăng xuất bằng mật khẩu, Google Authenticate API, quản lý thông tin cá nhân, bảo mật dữ liệu, cũng như kiểm soát tài khoản bằng admin permission.

Đề tài hướng đến việc xây dựng một ứng dụng flashcards thông minh với các mục tiêu cụ thể như sau:

- **Tạo flashcards cá nhân hóa**: Người dùng có thể tự tạo và quản lý các bộ flashcards phù hợp với nhu cầu học tập của mình.
- **Chế độ học linh hoạt**: Ứng dụng cung cấp nhiều chế độ học khác nhau, bao gồm học bằng thẻ lật, bài kiểm tra trắc nghiệm nhiều lựa chọn (multiple choice quiz) và câu hỏi đúng/sai (true-false question).
- **Chia sẻ flashcards giữa người dùng**: Cho phép người dùng chia sẻ các bộ flashcards của mình với bạn bè, đồng nghiệp hoặc cộng đồng, khuyến khích sự tương tác và học hỏi lẫn nhau.

---

### **2. Các tính năng chính (Mô tả chi tiết):**

#### **2.1. Quản lý tài khoản người dùng:**

- **Đăng ký, đăng nhập và đăng xuất:**
    - Người dùng tạo tài khoản với email và mật khẩu.
    - Mật khẩu được harsh khi đăng ký và so sánh với encrypted password khi đăng nhập
    - Chức năng "Quên mật khẩu" để đặt lại mật khẩu thông qua email.
    - Chức năng đăng nhập bằng email

- **Thông tin cá nhân:**
    - Hiển thị thông tin người dùng (ảnh đại diện, tên, email).
    - Người dùng có thể cập nhật thông tin cá nhân và thay đổi mật khẩu.
	    - Chức năng “Cập nhật mật khẩu“
	    - Chức năng “Cập nhật email”
	    - Chức năng “Đổi username”

#### **2.2. Quản lý nội dung học tập:**

**a. CRUD Flashcard:**
- Người dùng có thể:
    - **Tạo** flashcard mới (2 mặt của flashcard và flashcard pack với description).
    - **Chỉnh sửa** flashcard: Thay đổi nội dung câu hỏi, câu trả lời
    - **Xóa** flashcard nếu không cần thiết.
    - View các flashcard đã tạo trong các pack
    - Chỉnh sửa publicity của pack
    - Restart nội dung bài học để ôn bài/ kiểm tra lại
    - Có thể share pack cho các user khác
    - Lập tức hiển thị lại kết quả đúng khi làm sai

**b. CRUD Folder:**
- **Tạo folder:** Người dùng nhóm flashcard theo các chủ đề học tập khác nhau.
- **Chỉnh sửa folder:** Đổi tên hoặc description giữa các folder.
- **Xóa folder:** Cho phép xóa folder, đồng thời cảnh báo trước khi xóa.
- Share folder
- View folder

**c. CRUD Classroom:**
- **Tạo lớp học:** Một lớp học chứa nhiều folder, mỗi lớp có thể đại diện cho một môn học.
- Có thể thêm bộ thẻ vào lớp học
- Có thể chỉnh tên và mô tả lớp học
- Có thể tùy chỉnh publicity
- **Thành viên trong lớp học (Collaborator):**
    - Người dùng có thể mời bạn bè tham gia lớp học.
    - Chia sẻ folder/flashcard với thành viên trong lớp.
    - Người tham gia lớp học có thể out khỏi lớp học

---

#### **2.3. Chế độ học tập:**

**a. Lật thẻ (Flashcard):**
- Người dùng học theo từng flashcard bằng cách lật thẻ để xem câu trả lời.

**b. Chế độ quiz:**
- **Multiple Choice Quiz:**
- **True/False Quiz:**
==giải thích đáp án ngay khi làm sai==

---

#### **2.4. Báo cáo kết quả học tập:**

- Hiển thị báo cáo kết quả học tập:
    - Số lượng flashcard đã học, số câu đúng/sai, tỉ lệ câu đúng/sai .

---

#### **2.5. Tính năng nâng cao (Mở rộng):**

- Manage user,classroom với admin permission

---

### **3. Master plan:**

1. **Phân tích yêu cầu:**
    - Lên danh sách chức năng chi tiết.
    - Thiết kế luồng hoạt động và giao diện cho ứng dụng.
1. **Thiết kế hệ thống:**
    - Thiết kế cơ sở dữ liệu:
3. **Phát triển ứng dụng:**
    - Xây dựng module đăng nhập/đăng xuất và quản lý flashcard.
    - Phát triển các chế độ học tập (lật thẻ, quiz).
    - Tích hợp các tính năng mở rộng như đồng bộ hóa và thông báo.
4. **Kiểm thử và hoàn thiện:**
    - Kiểm tra tính ổn định của các chức năng.
    - Tối ưu hiệu năng và giao diện.
5. **Triển khai:**
    - Đóng gói ứng dụng và cài đặt trên thiết bị thử nghiệm.
    - Viết báo cáo tổng kết đồ án.

---

### **4. Kết quả đầu ra:**

- Mockup figma
- Mã nguồn dự án ver 1 với các tính năng cơ bản của 1 app flashcard
- Mã nguồn dự án ver 2 có cải tiến về tính năng và trải nghiệm 
- Tài liệu SRS, BRD, ERD
- Bản báo cáo bài tập lớn Word
- Bản thuyết trình Canva 
- Báo cáo testing
---

### **5. Định hướng phát triển trong tương lai:**
#### 5.1. **Tính năng mới:**

#### 5.2. Tối ưu hóa trải nghiệm người dùng:
- Dynamic pagination 
	- Tối ưu truy vấn dữ liệu khi có số lượng lớn flashcard hoặc folder.
	- Pagination động cho phép người dùng điều chỉnh số lượng flashcard hiển thị trên một trang và chọn trang bất kỳ trong danh sách.
	- **Mục tiêu:** Giảm thời gian tải và tăng hiệu quả sử dụng tài nguyên.
	- Có thể chủ động chọn page number trong pagination khi cần lướt qua nhiều page 1 cách nhanh chóng và to head/end page
	![[Pasted image 20241127045842.png]]
	- Pagination hiện tất cả các page 
	![[Pasted image 20241127130426.png]]

- Thêm theme màn sáng/tối thủ công và sáng/tối dựa trên theme hiện tại của thiết bị
 - Lưu và đồng bộ usersetting vào local database (sqlite) và sync lên firebase để tránh người dùng phải đổi lại setting với từng thiết bị
- **Đánh dấu flashcard yêu thích** để ôn lại.
- **Tích hợp thông báo nhắc nhở:**
    - Gửi thông báo nhắc nhở ôn tập flashcard, hoàn thành mục tiêu học tập hoặc khi classroom có folder, pack mới
- **Hệ thống gợi ý (ML-based):**
    - Dựa trên lịch sử học, sử dụng các thuật toán như Spaced Repetition hoặc SM-2( phương thức tương tự với anki)  gợi ý các flashcard phù hợp với khả năng và tiến độ của người dùng
    - Ưu tiên hỏi flashcard người dùng thường trả lời sai trong quiz.
- Biểu đồ thống kê tiến độ học tập theo ngày, tuần, hoặc tháng.
- Lịch sử học tập: Danh sách các phiên học trước đó.
- **Sync data lên Firebase:**
        - Khó khăn:
            - Xác định **primary database**: Firebase (ưu tiên đồng bộ dữ liệu giữa các thiết bị) hay SQLite (ưu tiên hiệu năng offline).
            - Giải quyết xung đột dữ liệu khi có thay đổi từ nhiều thiết bị.
        - **Giải pháp:**
            - Firebase được chọn làm primary database, đảm bảo mọi dữ liệu cuối cùng đều được đồng bộ.
            - Sử dụng timestamp để xác định dữ liệu mới nhất khi xảy ra xung đột.
- Tạo môi trường thi đua trực tuyến:
    - Người dùng tham gia các bài quiz chung với bạn bè trong classroom
    - Giới hạn thời gian cho quiz
    - Điểm số dựa trên độ chính xác của đáp số, streak và tốc độ trả lời.
	- Bảng xếp hạng thời gian thực:
	    - Hiển thị thành tích của người dùng để tăng cạnh tranh 
	    
#### 5.3 Thêm luồng CI/CD để tối ưu hóa quá trình build, test, và deploy ứng dụng.
- Xây dựng pipeline tự đống hóa các quy trình bằng jenkins, github actions
	-  **Continuous Integration (CI):** Tự động hóa kiểm tra mã nguồn mới, build ứng dụng, và chạy unit test mỗi khi có thay đổi mã nguồn.
	- **Continuous Deployment (CD):** Tự động hóa việc triển khai ứng dụng tới môi trường staging hoặc release build APK/AAB để phân phối.
--> tiết kiệm thời gian phát triển dự án

Đánh giá dự án: có không gian để phát triển thêm

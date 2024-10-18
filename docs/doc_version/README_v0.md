<h1 align="center">Ứng dụng thẻ lật</h1>

Thật khó để có thể chọn một đề tài phù hợp cho dự án một. Qua tìm hiểu và nghiên cứu các ứng dụng hiện nay trên cửa hàng ứng dụng, nhóm chúng em thấy có rất nhiều ứng dụng hay như ứng dụng thương mại điện tử, sức khoẻ, tài chính, mạng xã hội, giáo dục. Nhưng đa số nó đều khá khó để thực hiện, đòi hỏi phải bỏ thời gian, tiền bạc và công sức ra. Sau một ngày nghiên cứu và tìm tòi nhóm chúng em quyết định sẽ làm một ứng dụng giúp cho học sinh, giáo viên, người đi làm hoặc bất kỳ ai có thể nhanh chóng ghi nhớ kiến thức chẳng hạn như những câu lý thuyết dài, những từ vựng, cấu trúc ngoại ngữ,.. và đồng thời nhóm em cũng cảm thấy ứng dụng phù hợp với yêu cầu của dự án, có thể phát triển lâu dài. 

# Mục lục

- [Mục lục](#mục-lục)
- [Mô tả hoạt động của hệ thống](#mô-tả-hoạt-động-của-hệ-thống)
- [Luồng dữ liệu](#luồng-dữ-liệu)
- [trong biểu đồ usecase](#trong-biểu-đồ-usecase)
- [giao diện](#giao-diện)
- [Backend: java + kolin](#backend-java--kolin)

  - [Bảo mật và quản lý dữ liệu](#bảo-mật-và-quản-lý-dữ-liệu)//

# mô tả hoạt động của hệ thống 

1. **Màn hình chào**
   - Khi người dùng truy cập vào hệ thống, họ sẽ thấy màn hình chào.
   
2. **Màn hình đăng nhập và đăng ký**
   - Người dùng có thể lựa chọn đăng nhập hoặc đăng ký:
     - **Màn hình đăng nhập**: Nếu đã có tài khoản.
     - **Màn hình đăng ký**: Nếu chưa có tài khoản.
     
3. **Phân quyền (role)**
   - Sau khi đăng nhập, hệ thống xác định vai trò (role) của người dùng:
     - **Admin**
     - **Người dùng (giáo viên hoặc học viên)**

4. **Chức năng của Admin**
   - Admin sau khi đăng nhập sẽ được chuyển tới màn hình chính của Admin, với hai chức năng chính:
     - **Quản lý user**: Quản lý thông tin của người dùng trong hệ thống.
     - **Quản lý học phần**: Quản lý các học phần trong hệ thống.

5. **Chức năng của người dùng**
   - Người dùng (có thể là giáo viên hoặc học viên) sẽ vào màn hình chính sau khi đăng nhập, với các lựa chọn:
     - **Hồ sơ**: Xem và chỉnh sửa thông tin cá nhân.
     - **Học phần**: Quản lý hoặc đăng ký các học phần.
     - **Lớp học**: Dành riêng cho giáo viên để quản lý lớp học.
     - **Khóa học**: Tham gia các khóa học.
     - **Thành tựu**: Xem thành tích của bản thân.
     - **Thêm**: Các tính năng hoặc phần bổ sung khác.

6. **Vai trò của Giáo viên**
   - Nếu người dùng là giáo viên, họ sẽ có thêm quyền quản lý lớp học và học phần.


## luồng dữ liệu
## trong biểu đồ usecase
## Các tác nhân (Actors):
- **Giáo viên**: Người dạy, quản lý các lớp học và tài liệu học tập (flashcards, thư mục).
- **Học sinh**: Người học, sử dụng hệ thống để truy cập tài liệu và quản lý tài khoản cá nhân.
- **Admin**: Quản trị viên hệ thống, có quyền quản lý người dùng và các chức năng cốt lõi khác.

## Các trường hợp sử dụng chính:
### 1. Đăng nhập
   - **Mô tả**: Tất cả người dùng (Giáo viên, Học sinh, Admin) phải đăng nhập vào hệ thống để sử dụng các chức năng.
   - **Mối quan hệ**: 
     - **Include**: Đăng nhập là bước bắt buộc để truy cập các chức năng khác.
     - **Extend**: Có thể mở rộng với các chức năng khác như đăng ký, đăng xuất, đổi mật khẩu.

### 2. Quản lý người dùng (Admin)
   - **Mô tả**: Admin quản lý thông tin và quyền hạn của người dùng trong hệ thống.
   - **tác nhân liên quan**: Admin.
   - **Mối quan hệ**: Bao gồm trong trường hợp đăng nhập.

### 3. Quản lý lớp học (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo, quản lý các lớp học.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 4. Quản lý flashcards (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo và quản lý các bộ flashcards phục vụ học tập.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 5. Quản lý thư mục (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo và quản lý thư mục chứa flashcards hoặc tài liệu học tập.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 6. Tạo flashcards (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo flashcards mới để học sinh sử dụng.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 7. Tạo thư mục (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo thư mục để lưu trữ flashcards hoặc tài liệu khác.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 8. Tạo lớp học (Giáo viên)
   - **Mô tả**: Giáo viên có thể tạo lớp học và thêm học sinh vào lớp.
   - **tác nhân liên quan**: Giáo viên.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 9. Hiển thị flashcards, thư mục (Học sinh)
   - **Mô tả**: Học sinh có thể xem flashcards và thư mục do giáo viên tạo.
   - **tác nhân liên quan**: Học sinh.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 10. Hồ sơ tài khoản (Học sinh)
   - **Mô tả**: Học sinh có thể xem và chỉnh sửa thông tin tài khoản cá nhân.
   - **tác nhân liên quan**: Học sinh.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 11. Thư viện (Học sinh)
   - **Mô tả**: Học sinh có thể truy cập thư viện tài liệu học tập.
   - **tác nhân liên quan**: Học sinh.
   - **Mối quan hệ**: Include trong trường hợp đăng nhập.

### 12. Đăng ký
   - **Mô tả**: Người dùng có thể đăng ký tài khoản mới nếu chưa có.
   - **tác nhân liên quan**: Tất cả người dùng.
   - **Mối quan hệ**: Extend từ trường hợp đăng nhập.

### 13. Đăng xuất
   - **Mô tả**: Người dùng có thể đăng xuất khỏi hệ thống.
   - **tác nhân liên quan**: Tất cả người dùng.
   - **Mối quan hệ**: Extend từ trường hợp đăng nhập.

### 14. Đổi mật khẩu
   - **Mô tả**: Người dùng có thể thay đổi mật khẩu khi cần thiết.
   - **tác nhân liên quan**: Tất cả người dùng.
   - **Mối quan hệ**: Extend từ trường hợp đăng nhập.

## trong biểu đồ đăng ký

## giao diện

```
giao diện/
├── java (generated)
│   └── res
│       ├── anim
│       ├── drawable
│       ├── font
│       ├── layout
│       ├── menu
│       ├── mipmap
│       ├── navigation
│       ├── raw
│       ├── values
│       └── xml
└── res (generated)
    └── values
```

- **Giải thích chi tiết:**
  -  **`app/java (generated)`**:
   - Chứa các tài nguyên (resources) được tạo ra tự động trong dự án, bao gồm:
   - **`anim`**: Tài nguyên animation.
   - **`drawable`**: Tài nguyên hình ảnh (drawable).
   - **`font`**: Tài nguyên phông chữ.
   - **`layout`**: Tài nguyên bố cục giao diện (layout).
   - **`menu`**: Tài nguyên menu.
   - **`mipmap`**: Tài nguyên biểu tượng ứng dụng với nhiều kích thước.
   - **`navigation`**: Tài nguyên điều hướng (navigation).
   - **`raw`**: Tài nguyên thô (như âm thanh hoặc video).
   - **`values`**: Chứa các giá trị kiểu như màu sắc, kích thước, và chuỗi văn bản.
   - **`xml`**: Các tệp XML khác có thể được sử dụng trong ứng dụng.


## Backend: java + kolin

```
backend/
app
├── manifests
│   └── AndroidManifest.xml
├── kotlin+java
│   └── com
│       └── kewwi
│           └── quickmem
│               ├── adapter
│               ├── data
│               ├── preferen
│               ├── ui
│               └── utils
├── com (androidTest)
│   └── kewwi
│       └── quickmem
│           └── ExampleInstrumented
├── com (test)
│   └── kewwi
│       └── quickmem
│           └── ExampleUnitTest
```

- **Giải thích chi tiết:**
  - **`app/manifests`**:
   - Chứa tệp `AndroidManifest.xml`, nơi định nghĩa các thành phần của ứng dụng, quyền, và cấu hình chung.

 **`app/kotlin+java/com/kewwi/quickmem`**:
   - Đây là nơi chứa mã nguồn chính của ứng dụng.
   - **`adapter`**: Có thể chứa các adapter cho RecyclerView hoặc ListView.
   - **`data`**: Có thể chứa các lớp dữ liệu, như các model hoặc lớp quản lý dữ liệu (repository).
   - **`preferen`**: Có thể chứa các lớp để quản lý SharedPreferences hoặc các cấu hình khác.
   - **`ui`**: Chứa các lớp giao diện người dùng, như Activities, Fragments, hoặc các thành phần giao diện khác.
   - **`utils`**: Có thể chứa các lớp hoặc phương thức tiện ích để hỗ trợ các chức năng chung trong ứng dụng.

 **`app/com/androidTest`**:
   - Chứa các bài kiểm tra kiểm thử tích hợp (instrumented tests) cho ứng dụng.
   - **`ExampleInstrumented`**: Có thể là một lớp kiểm thử mẫu để thử nghiệm chức năng của ứng dụng trên thiết bị hoặc emulator.

 **`app/com/test`**:
   - Chứa các bài kiểm tra đơn vị (unit tests) cho ứng dụng.
   - **`ExampleUnitTest`**: Là một lớp kiểm thử mẫu cho các bài kiểm tra đơn vị.




## Bảo mật và quản lý dữ liệu

- 
- 
- 
- 